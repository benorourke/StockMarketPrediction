package net.ben.stocks.framework.thread;

import net.ben.stocks.framework.util.Nullable;
import net.ben.stocks.framework.util.ThreadSynchronised;

import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

/**
 * Handles all the grizzly inner-workings of a Task implementation.
 */
public class TaskWrapper implements Runnable
{
    private final TaskManager taskManager;
    private final UUID id;
    private final Task task;
    private final ResultCallback resultCallback;

    @Nullable
    private ScheduledFuture handle;

    public TaskWrapper(TaskManager taskManager, Task task, ResultCallback resultCallback)
    {
        this.taskManager = taskManager;
        this.id = UUID.randomUUID();
        this.task = task;
        this.resultCallback = resultCallback;
    }

    @ThreadSynchronised
    @Override
    public void run()
    {
        task.run();
        if(task.isFinished())
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

    public void setHandle(ScheduledFuture handle)
    {
        this.handle = handle;
    }

    public ResultCallback getResultCallback()
    {
        return resultCallback;
    }

}
