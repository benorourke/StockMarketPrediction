package net.ben.stocks.framework.task;

import java.util.UUID;
import java.util.concurrent.*;

public class TaskManager
{
    private ScheduledExecutorService executor;
    private BlockingQueue<Result> results;

    public TaskManager()
    {
        // TODO: Make the corePoolSize configurable
        executor = executor = Executors.newScheduledThreadPool(5);
    }

    public void scheduleRepeating(Task task, long initialDelay,
                                  long period, TimeUnit timeUnit)
    {
        final ScheduledFuture<?> handle =
                executor.scheduleAtFixedRate(task,
                                             initialDelay,
                                             period,
                                             timeUnit);
        task.setHandle(handle);
    }


}
