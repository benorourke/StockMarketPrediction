package net.benorourke.stocks.userinterface;

import net.benorourke.stocks.framework.Configuration;
import net.benorourke.stocks.framework.Framework;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BackgroundThread extends Thread
{
    private final Framework framework;
    private final BlockingQueue<BackgroundRunnable> runnables;
    private final long sleepDelay;

    public BackgroundThread(Configuration configuration, long sleepDelay)
    {
        this.framework = new Framework(configuration);
        this.runnables = new LinkedBlockingQueue<>();
        this.sleepDelay = sleepDelay;
    }

    @Override
    public void run()
    {
        framework.initialise();

        while (true)
        {
            // Loop through all enqueued BackgroundRunnables to be executed on the Framework
            while (!runnables.isEmpty())
            {
                try
                {
                    BackgroundRunnable next = runnables.take();
                    next.run(framework);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            // Consume all the Task callbacks within the Framework
            framework.getTaskManager().consumeCallbacks();

            // Sleep before next thread iteration
            try
            {
                Thread.sleep(sleepDelay);
            }
            catch (InterruptedException exception)
            {
                exception.printStackTrace();
            }
        }
    }

    public void queueRunnable(BackgroundRunnable runnable)
    {
        runnables.add(runnable);
    }

}
