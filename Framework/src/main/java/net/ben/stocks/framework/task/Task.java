package net.ben.stocks.framework.task;

import net.ben.stocks.framework.util.Nullable;

import java.util.concurrent.ScheduledFuture;

public abstract class Task implements Runnable
{
    private final TaskType type;

    @Nullable
    private ScheduledFuture handle;

    public Task(TaskType type)
    {
        this.type = type;
    }

    public void cancel()
    {
        if(hasHandle()) handle.cancel(true);
    }

    public TaskType getType()
    {
        return type;
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
}
