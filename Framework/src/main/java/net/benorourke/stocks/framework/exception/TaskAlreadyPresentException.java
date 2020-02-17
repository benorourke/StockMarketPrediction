package net.benorourke.stocks.framework.exception;

import net.benorourke.stocks.framework.thread.Task;

public class TaskAlreadyPresentException extends Exception
{
    private final Task triedToSchedule;

    public TaskAlreadyPresentException(Task triedToSchedule)
    {
        super("Tried to schedule a task, but a task with the same description is already queued/running");

        this.triedToSchedule = triedToSchedule;
    }

    public Task getTriedToSchedule()
    {
        return triedToSchedule;
    }
}
