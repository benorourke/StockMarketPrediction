package net.benorourke.stocks.framework.thread;

import net.benorourke.stocks.framework.Configuration;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.exception.TaskStartException;
import net.benorourke.stocks.framework.util.ThreadSynchronised;
import net.benorourke.stocks.framework.util.Tuple;

import java.util.*;
import java.util.concurrent.*;

/**
 * The manager for all concurrency tasks.
 *
 * Do not call any of these functions from within a Task.
 *
 * Update the Progress/Result objects within Tasks to pass data to the main thread.
 */
public class TaskManager
{
    /** The thread pool*/
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
    /**
     * Stores the progresses of every currently ongoing task.
     *
     * Further facilitates parallelism by reducing the calls on {@link #taskMap}.
     */
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
     * Check whether a Task with a description that would collide with this Task is already present.
     *
     * @param task the task's description to check
     * @return whether there is a colliding task already scheduled
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
     * Schedule a task to be run by the thread pool executor.
     *
     * @param task the task
     * @param onFinished the callback once the task has finished
     * @param initialDelay the delay before running the task for the first time
     * @param period the delay between running the tasks
     * @param timeUnit the time unit for the above delays
     * @param <S> the type of the TaskDescription used to compare colliding tasks
     * @param <U> the type of the Result for this task
     * @return the ID of the task (wrapper)
     * @throws TaskStartException if a task with a colliding TaskDescription is already present
     */
    @ThreadSynchronised
    public <S extends TaskDescription,
            U extends Result> UUID scheduleRepeating(Task<S, U> task, ResultCallback<U> onFinished,
                                                     long initialDelay, long period, TimeUnit timeUnit)
            throws TaskStartException
    {
        if (isTaskPresent(task))
            throw new TaskStartException(task);

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
     * Get the progress for a given task.
     *
     * Do not regularly call - instead maintain a reference; the percentage field is volatile.
     *
     * @param taskId the task to get the progress instance of
     * @return the progress object
     */
    @ThreadSynchronised
    public Progress getProgress(UUID taskId)
    {
        return progressMap.get(taskId);
    }

    /**
     * Cancel a task by it's (wrapper) ID.
     *
     * @param taskId it's unique identifier
     */
    @ThreadSynchronised
    public void cancel(UUID taskId)
    {
        if (!taskMap.containsKey(taskId)) return;

        TaskWrapper wrapper = taskMap.get(taskId);
        if(wrapper.hasHandle()) wrapper.getHandle().cancel(true);

        taskMap.remove(taskId);
        descriptionMap.remove(taskId);
        progressMap.remove(taskId);
    }

    /**
     *
     * @param wrapper
     */
    @ThreadSynchronised
    public void onTaskFinished(TaskWrapper wrapper)
    {
        if(wrapper.hasHandle()) wrapper.getHandle().cancel(true);

        callbackQueue.add(new Tuple<>(wrapper, wrapper.getResultCallback()));
    }

    /**
     * Call this frequently on the main framework thread in order to consume and run the callbacks for
     * completed tasks.
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

    /**
     * Clones the Map, but not the elements within.
     *
     * @return the cloned map of task descriptions
     */
    @ThreadSynchronised
    public Map<UUID, TaskDescription> cloneDescriptionMap()
    {
        Map<UUID, TaskDescription> map = new LinkedHashMap<>();
        for (Map.Entry<UUID, TaskDescription> entry : descriptionMap.entrySet())
            map.put(entry.getKey(), entry.getValue());
        return map;
    }

    /**
     * Clones the Map, but not the elements within.
     *
     * @return the cloned map of task progresses
     */
    @ThreadSynchronised
    public Map<UUID, Progress> cloneProgressMap()
    {
        Map<UUID, Progress> map = new LinkedHashMap<>();
        for (Map.Entry<UUID, Progress> entry : progressMap.entrySet())
            map.put(entry.getKey(), entry.getValue());
        return map;
    }


}
