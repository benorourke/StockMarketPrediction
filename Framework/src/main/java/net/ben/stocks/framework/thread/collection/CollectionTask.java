package net.ben.stocks.framework.thread.collection;

import net.ben.stocks.framework.thread.Task;
import net.ben.stocks.framework.thread.Progress;

import java.util.Random;

public class CollectionTask implements Task<CollectionResult>
{
    private Progress progress;
    private final String name;

    public CollectionTask(String name)
    {
        this.name = name;
    }

    @Override
    public Progress createTaskProgress()
    {
        return progress = new Progress();
    }

    @Override
    public void run()
    {
        System.out.println(name + " running");
        progress.setProgress(new Random().nextInt());
    }

    @Override
    public boolean isFinished()
    {
        return true;
    }

    @Override
    public CollectionResult getResult()
    {
        return new CollectionResult();
    }

}
