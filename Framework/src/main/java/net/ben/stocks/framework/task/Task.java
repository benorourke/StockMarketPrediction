package net.ben.stocks.framework.task;

public class Task extends Thread
{
    private volatile boolean stopped;

    public Task(Runnable runnable)
    {
        super(runnable);

        stopped = false;
    }

    public boolean isStopped()
    {
        return stopped;
    }

    public void setStopped(boolean stopped)
    {
        this.stopped = stopped;
    }

}
