package net.benorourke.stocks.userinterface;

import net.benorourke.stocks.framework.Configuration;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.thread.Progress;
import net.benorourke.stocks.framework.thread.TaskDescription;
import net.benorourke.stocks.framework.thread.TaskManager;
import net.benorourke.stocks.framework.util.ThreadSynchronised;
import net.benorourke.stocks.framework.util.Tuple;
import net.benorourke.stocks.userinterface.util.Constants;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BackgroundThread extends Thread
{
    private final Framework framework;
    private final Set<TaskUpdateAdapter> adapters;

    private final BlockingQueue<BackgroundRunnable> runnables;
    private final BlockingQueue<Tuple<TaskUpdateAdapter, Boolean>> adapterChanges;
    private final long sleepDelay;

    private long lastUpdatedTasks;

    public BackgroundThread(Configuration configuration, long sleepDelay)
    {
        this.framework = new Framework(configuration);
        this.adapters = new HashSet<>();

        this.runnables = new LinkedBlockingQueue<>();
        this.adapterChanges = new LinkedBlockingQueue<>();
        this.sleepDelay = sleepDelay;

        this.lastUpdatedTasks = System.currentTimeMillis();
    }

    @ThreadSynchronised
    @Override
    public void run()
    {
        framework.initialise();

        while (true)
        {
            long now = System.currentTimeMillis();

            runBackgroundRunnables();
            if (now - lastUpdatedTasks >= Constants.UPDATE_TASKS_EVERY)
                checkTaskUpdates();
            // Consume all the Task callbacks within the Framework
            framework.getTaskManager().consumeCallbacks();

            // Sleep before next thread iteration
            try
            {
                Thread.sleep(sleepDelay);
            }
            catch (InterruptedException exception)
            {
                StockApplication.error("Unable to sleep BackgroundThread", exception);
            }
        }
    }

    /**
     * Loop through all enqueued BackgroundRunnables to be executed on the Framework.
     */
    @ThreadSynchronised
    public void runBackgroundRunnables()
    {
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
    }

    /**
     * Makes any queued changes to the adapters necessary, then updates all of the adapters with respective
     * progresses, etc.
     */
    @ThreadSynchronised
    public void checkTaskUpdates()
    {
        // Make changes
        while (!adapterChanges.isEmpty())
        {
            try
            {
                Tuple<TaskUpdateAdapter, Boolean> next = adapterChanges.take();
                if (next.getB())
                    adapters.add(next.getA());
                else
                    adapters.remove(next.getB());
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        // Give updates to adapters
        TaskManager taskManager = framework.getTaskManager();
        Map<UUID, TaskDescription> descriptions = taskManager.cloneDescriptionMap();
        Map<UUID, Progress> progresses = taskManager.cloneProgressMap();
        for (TaskUpdateAdapter adapter : adapters)
        {
            // Clone the instances in case any of the adapters alter the values within
            Map<UUID, TaskDescription> descClone = new HashMap<>(descriptions);
            Map<UUID, Progress> progClone = new HashMap<>(progresses);

            adapter.update(descClone, progClone);
        }
    }

    @ThreadSynchronised
    public void queueRunnable(BackgroundRunnable runnable)
    {
        runnables.add(runnable);
    }

    /**
     *
     * @param adapter
     * @param addOrRemove if true, adapter will be added, otherwise removed
     */
    @ThreadSynchronised
    public void queueAdapterChange(TaskUpdateAdapter adapter, boolean addOrRemove)
    {
        adapterChanges.add(new Tuple<>(adapter, addOrRemove));
    }

}
