package net.ben.stocks.framework.thread.internals;

import net.ben.stocks.framework.Configuration;
import net.ben.stocks.framework.Framework;
import net.ben.stocks.framework.thread.Result;
import net.ben.stocks.framework.thread.ResultCallback;
import net.ben.stocks.framework.thread.Task;
import net.ben.stocks.framework.thread.Progress;
import net.ben.stocks.framework.util.ThreadSynchronised;
import net.ben.stocks.framework.util.Tuple;

import java.util.*;
import java.util.concurrent.*;

public class TaskManager
{
    @ThreadSynchronised
    private final ScheduledExecutorService executor;
    @ThreadSynchronised
    private final ConcurrentHashMap<UUID, Tuple<TaskWrapper, Progress>> taskMap;
    /**
     * Callbacks are stored here once tasks are finished.
     *
     * An instance of the callback's respective TaskWrapper is also maintained in {@link#taskMap} until it is
     * consumed by {@link#consumeCallbacks(Framework)}.
     */
    @ThreadSynchronised
    private final BlockingQueue<TaskCallback> callbackQueue;

    public TaskManager(Configuration configuration)
    {
        // TODO: Make the corePoolSize configurable
        executor = Executors.newScheduledThreadPool(configuration.getTaskPoolSize());
        taskMap = new ConcurrentHashMap<>();
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
        taskMap.put(wrapper.getId(), new Tuple<>(wrapper, task.newTaskProgress()));

        final ScheduledFuture<?> handle =
                executor.scheduleAtFixedRate(wrapper,
                                             initialDelay,
                                             period,
                                             timeUnit);
        wrapper.setHandle(handle);

        return wrapper.getId();
    }

    @ThreadSynchronised
    public double getProgress(UUID taskId)
    {
        return taskMap.get(taskId).getB().getProgress();
    }

    @ThreadSynchronised
    public void cancel(UUID taskId)
    {
        // TODO
    }

    @ThreadSynchronised
    public void onTaskFinished(TaskWrapper wrapper)
    {
        if(wrapper.hasHandle()) wrapper.getHandle().cancel(true);

        callbackQueue.add(wrapper.getTask().getFinishedCallback());
    }

    /**
     * Only to be called by a single, main thread in order to get the results / callbacks of tasks.
     */
    @ThreadSynchronised
    public void consumeCallbacks(Framework framework)
    {
        // Store them here and remove once we've got them all in case of a deadlock
        Queue<TaskWrapper> tasksToRemove = new LinkedList<TaskWrapper>();

        while (!callbackQueue.isEmpty())
        {
            try
            {
                TaskCallback callback = callbackQueue.take();
                tasksToRemove.add(callback.getTaskWrapper());
                callback.onCallback(framework);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        // Remove the Task from the taskMap
        while (!tasksToRemove.isEmpty())
        {
            TaskWrapper wrapper = tasksToRemove.peek();
            wrapper.getResultCallback().onCallback(wrapper.getTask().getResult());
            taskMap.remove(tasksToRemove.poll().getId());
        }
    }


}
