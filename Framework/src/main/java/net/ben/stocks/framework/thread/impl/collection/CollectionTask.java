package net.ben.stocks.framework.thread.impl.collection;

import net.ben.stocks.framework.thread.Task;
import net.ben.stocks.framework.thread.Progress;

public class CollectionTask implements Task<CollectionResult>
{

    private final String name;

    public CollectionTask(String name)
    {
        this.name = name;
    }

    @Override
    public Progress newTaskProgress()
    {
        return new Progress();
    }

    @Override
    public void run()
    {
        System.out.println(name + " running");
    }

    @Override
    public boolean isFinished()
    {
        return false;
    }

    @Override
    public CollectionResult getResult()
    {
        return new CollectionResult();
    }

}
