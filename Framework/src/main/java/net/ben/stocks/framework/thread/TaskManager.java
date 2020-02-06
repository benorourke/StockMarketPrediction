package net.ben.stocks.framework.thread;

import net.ben.stocks.framework.Configuration;
import net.ben.stocks.framework.Framework;
import net.ben.stocks.framework.util.ThreadSynchronised;
import net.ben.stocks.framework.util.Tuple;

import java.util.*;
import java.util.concurrent.*;

/**
 *
 */
public class TaskManager
{
    @ThreadSynchronised
    private final ScheduledExecutorService executor;
    /**
     * Stores currently on-going Tasks, along with finished tasks (until they are consumed by
     * {@link#consumeCallbacks(Framework})
     */
    @ThreadSynchronised
    private final ConcurrentHashMap<UUID, TaskWrapper> taskMap;
    @ThreadSynchronised
    private final ConcurrentHashMap<UUID, Progress> progressMap;
    /**
     * Callbacks are stored here once tasks are finished.
     *
     * An instance of the callback's respective TaskWrapper is also maintained in {@link#taskMap} until it is
     * consumed by {@link#consumeCallbacks(Framework)}.
     */
    @ThreadSynchronised
    private final BlockingQueue<Tuple<TaskWrapper, ResultCallback>> callbackQueue;

    public TaskManager(Configuration configuration)
    {
        Framework.info("Creating task thread pool [" +configuration.getTaskPoolSize() + "]");

        executor = Executors.newScheduledThreadPool(configuration.getTaskPoolSize());
        taskMap = new ConcurrentHashMap<>();
        progressMap = new ConcurrentHashMap<>();
        callbackQueue = new LinkedBlockingQueue<>();
    }

    /**
     *
     * @param task
     * @param onFinished
     * @param initialDelay
     * @param period
     * @param timeUnit
     * @param <T>
     * @return the taskId
     */
    @ThreadSynchronised
    public <T extends Result> UUID scheduleRepeating(Task<T> task, ResultCallback<T> onFinished,
                                                     long initialDelay, long period, TimeUnit timeUnit)
    {
        TaskWrapper wrapper = new TaskWrapper(this, task, onFinished);
        taskMap.put(wrapper.getId(), wrapper);
        progressMap.put(wrapper.getId(), task.createTaskProgress());

        final ScheduledFuture<?> handle =
                executor.scheduleAtFixedRate(wrapper,
                                             initialDelay,
                                             period,
                                             timeUnit);
        wrapper.setHandle(handle);
        return wrapper.getId();
    }

    /**
     * Do not regularly call - instead maintain a reference; the members are volatile anyway
     * @param taskId
     * @return
     */
    @ThreadSynchronised
    public Progress getProgress(UUID taskId)
    {
        return progressMap.get(taskId);
    }

    @ThreadSynchronised
    public void cancel(UUID taskId)
    {
        TaskWrapper wrapper = taskMap.get(taskId);
        if(wrapper.hasHandle()) wrapper.getHandle().cancel(true);
    }

    @ThreadSynchronised
    public void onTaskFinished(TaskWrapper wrapper)
    {
        if(wrapper.hasHandle()) wrapper.getHandle().cancel(true);

        callbackQueue.add(new Tuple<>(wrapper, wrapper.getResultCallback()));
    }

    /**
     * Only to be called by a single, main thread in order to get the results / callbacks of tasks.
     */
    @ThreadSynchronised
    public void consumeCallbacks()
    {
        // Store them here and remove once we've got them all in case of a deadlock
        Queue<TaskWrapper> tasksToRemove = new LinkedList<TaskWrapper>();

        while (!callbackQueue.isEmpty())
        {
            try
            {
                Tuple<TaskWrapper, ResultCallback> tuple = callbackQueue.take();
                TaskWrapper wrapper = tuple.getA();

                tasksToRemove.add(wrapper);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        // Remove the Task from the taskMap and progressMap
        while (!tasksToRemove.isEmpty())
        {
            TaskWrapper wrapper = tasksToRemove.peek();
            wrapper.getResultCallback().onCallback(wrapper.getTask().getResult());

            taskMap.remove(tasksToRemove.poll().getId());
            /**
             *  TODO: Is this thread-safe? Could cause deadlocks - should I clone the tasks and do this separately?
             */
            progressMap.remove(wrapper.getId());
        }
    }


}
