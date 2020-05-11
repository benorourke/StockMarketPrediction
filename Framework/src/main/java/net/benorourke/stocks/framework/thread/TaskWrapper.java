package net.benorourke.stocks.framework.thread;

import net.benorourke.stocks.framework.util.Nullable;
import net.benorourke.stocks.framework.util.ThreadSynchronised;

import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

/**
 * A wrapper object for more easily identifying & handling Task implementations.
 *
 * This class ultimately abstracts a lot of the TaskManager away from creating tasks.
 */
public class TaskWrapper implements Runnable
{
    /** The TaskManager handling this task. */
    private final TaskManager taskManager;
    /** The ID of the task. */
    private final UUID id;
    /** The task itself. */
    private final Task task;
    /** The callback once the Task has been completed. */
    private final ResultCallback resultCallback;

    /**
     * The thread pool handle injected into the TaskWrapper once this TaskWrapper has been scheduled.
     *
     * This also allows for this TaskWrapper to be removed from the pool upon {@link Task#isFinished()}.
     */
    @Nullable
    private ScheduledFuture handle;

    public TaskWrapper(TaskManager taskManager, Task task, ResultCallback resultCallback)
    {
        this.taskManager = taskManager;
        this.id = UUID.randomUUID();
        this.task = task;
        this.resultCallback = resultCallback;
    }

    /**
     * Handles calling the {@link Task#run()} function and also acts accordingly when it has finished.
      */
    @ThreadSynchronised
    @Override
    public void run()
    {
        task.run();
        if (task.isFinished())
            taskManager.onTaskFinished(this);
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof TaskWrapper && ((TaskWrapper) obj).id.equals(id);
    }

    @Override
    public String toString()
    {
        return "TaskID=" + id.toString();
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

    public UUID getId()
    {
        return id;
    }

    public Task getTask()
    {
        return task;
    }

    public ScheduledFuture getHandle()
    {
        return handle;
    }

    public boolean hasHandle()
    {
        return handle != null;
    }

    /**
     * Inject the thread pool handle into this wrapper so that the TaskWrapper can be removed from the pool upon
     * Task completion.
     *
     * @param handle the handle to inject
     */
    public void setHandle(ScheduledFuture handle)
    {
        this.handle = handle;
    }

    public ResultCallback getResultCallback()
    {
        return resultCallback;
    }

}
