package net.ben.stocks.framework.thread.internals;

import net.ben.stocks.framework.Framework;
import net.ben.stocks.framework.thread.Task;

import java.util.UUID;

/**
 * The Callback if the Task needs to run anything on the main thread once finished before calling the ResultCallback,
 * for example, writing some collected data to file.
 *
 * This is internal and specific to the framework.
 */
public abstract class TaskCallback
{
    private final TaskWrapper wrapper;

    protected TaskCallback(TaskWrapper wrapper)
    {
        this.wrapper = wrapper;
    }

    public abstract void onCallback(Framework framework);

    public TaskWrapper getTaskWrapper()
    {
        return wrapper;
    }

}
