package net.benorourke.stocks.framework.thread;

import net.benorourke.stocks.framework.Configuration;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.exception.TaskAlreadyPresentException;
import net.benorourke.stocks.framework.util.ThreadSynchronised;
import net.benorourke.stocks.framework.util.Tuple;

import java.util.*;
import java.util.concurrent.*;

/**
 * Do not call any of these functions from within a Task.
 *
 * Update the Progress/Result objects within Tasks to pass data to the main thread.
 */
public class TaskManager
{
    // TODO - Synchronise the methods that remove/add to queues using a single lock

    @ThreadSynchronised
    private final ScheduledExecutorService executor;
    /**
     * Stores currently on-going Tasks, along with finished tasks (until they are consumed by
     * {@link#consumeCallbacks(Framework})
     */
    @ThreadSynchronised
    private final ConcurrentHashMap<UUID, TaskWrapper> taskMap;
    /**
     * Stores the descriptions of every task.
     *
     * Further facilitates parallelism by reducing the calls on {@link #taskMap}.
     */
    @ThreadSynchronised
    private final ConcurrentHashMap<UUID, TaskDescription> descriptionMap;
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
        descriptionMap = new ConcurrentHashMap<>();
        progressMap = new ConcurrentHashMap<>();
        callbackQueue = new LinkedBlockingQueue<>();
    }

    /**
     * Check whether a Task with the same description is running / queued.
     * @param task
     * @return
     */
    @ThreadSynchronised
    public boolean isTaskPresent(Task task)
    {
        TaskDescription desc = task.getDescription();
        for (TaskDescription value : descriptionMap.values())
        {
            if (desc.equals(value))
                return true;
        }
        return false;
    }

    /**
     * Check whether a Task of a given type is running / queued.
     *
     * @param type
     * @return
     */
    @ThreadSynchronised
    public boolean isTaskPresent(TaskType type)
    {
        for (TaskDescription value : descriptionMap.values())
        {
            if (value.getType().equals(value))
                return true;
        }
        return false;
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
    public <S extends TaskDescription,
            U extends Result> UUID scheduleRepeating(Task<S, U> task, ResultCallback<U> onFinished,
                                                     long initialDelay, long period, TimeUnit timeUnit)
            throws TaskAlreadyPresentException
    {
        if (isTaskPresent(task))
            throw new TaskAlreadyPresentException(task);

        TaskWrapper wrapper = new TaskWrapper(this, task, onFinished);
        taskMap.put(wrapper.getId(), wrapper);
        descriptionMap.put(wrapper.getId(), wrapper.getTask().getDescription());
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

        // Three separate iterators so we can do them separately; preventing deadlocks
        Iterator<TaskWrapper> taskMapIterator = new LinkedList(tasksToRemove).iterator();
        Iterator<TaskWrapper> descriptionMapIterator = new LinkedList(tasksToRemove).iterator();
        Iterator<TaskWrapper> progressMapIterator = new LinkedList(tasksToRemove).iterator();

        while (taskMapIterator.hasNext())
        {
            TaskWrapper next = taskMapIterator.next();
            next.getResultCallback().onCallback(next.getTask().getResult());
            taskMap.remove(tasksToRemove.poll().getId());
        }

        while (descriptionMapIterator.hasNext())
            descriptionMap.remove(descriptionMapIterator.next().getId());

        while (progressMapIterator.hasNext())
            progressMap.remove(progressMapIterator.next().getId());
    }


}
