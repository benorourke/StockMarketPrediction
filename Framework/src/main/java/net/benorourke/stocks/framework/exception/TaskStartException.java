package net.benorourke.stocks.framework.exception;

import net.benorourke.stocks.framework.thread.Task;

/**
 * Exception thrown when a task cannot start because a clashing task is blocking it.
 */
public class TaskStartException extends Exception
{
    private final Task triedToSchedule;

    public TaskStartException(Task triedToSchedule)
    {
        super("Tried to schedule a task (" + triedToSchedule.getType() + "), but a task with the same description "
                        + "is already queued/running");

        this.triedToSchedule = triedToSchedule;
    }

    public Task getTriedToSchedule()
    {
        return triedToSchedule;
    }
}
