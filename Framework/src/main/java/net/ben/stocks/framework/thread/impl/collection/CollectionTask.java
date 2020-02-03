package net.ben.stocks.framework.thread.impl.collection;

import net.ben.stocks.framework.thread.Task;
import net.ben.stocks.framework.thread.Progress;
import net.ben.stocks.framework.thread.internals.TaskCallback;

public class CollectionTask implements Task<CollectionResult>
{

    @Override
    public Progress newTaskProgress()
    {
        return new Progress();
    }

    @Override
    public void run()
    {

    }

    @Override
    public boolean isFinished()
    {
        return false;
    }

    @Override
    public TaskCallback getFinishedCallback()
    {
        return null;
    }

    @Override
    public CollectionResult getResult()
    {
        return null;
    }

}
