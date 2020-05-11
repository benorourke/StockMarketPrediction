package net.benorourke.stocks.framework.thread.collection;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.collection.Query;
import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.collection.session.CollectionSession;
import net.benorourke.stocks.framework.collection.session.filter.CollectionFilter;
import net.benorourke.stocks.framework.exception.ConstraintException;
import net.benorourke.stocks.framework.exception.FailedCollectionException;
import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.thread.Progress;
import net.benorourke.stocks.framework.thread.Task;
import net.benorourke.stocks.framework.thread.TaskType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionTask<T extends Data> implements Task<CollectionDescription, CollectionResult<T>>
{
    private final DataSource<T> dataSource;
    private final CollectionSession<T> session;
    private final CollectionExceptionHook exceptionHook;

    /**
     * Elements are appended to the list as collected
     */
    private List<T> collected;
    private Progress progress;

    public CollectionTask(DataSource<T> dataSource, CollectionSession<T> session,
                          CollectionExceptionHook exceptionHook)
    {
        this.dataSource = dataSource;
        this.session = session;
        this.exceptionHook = exceptionHook;

        collected = new ArrayList<>();
    }

    @Override
    public TaskType getType()
    {
        return TaskType.COLLECTION;
    }

    @Override
    public CollectionDescription getDescription()
    {
        return new CollectionDescription(dataSource.getClass());
    }

    @Override
    public Progress createTaskProgress()
    {
        return progress = new Progress();
    }

    @Override
    public void run()
    {
        Query next = session.nextQuery();
        try
        {
            Collection<T> data = dataSource.retrieve(next);
            if (!data.isEmpty())
            {
                CollectionFilter<T> filter = session.getCollectionFilter();
                Collection<T> filtered = CollectionFilter.reduce(data, filter);
                collected.addAll(filtered);

                Framework.info("Collected " + filtered.size() + " data for " + next.toString());
            }
            else
                Framework.info("Collected no data for " + next.toString());
        }
        catch (ConstraintException e)
        {
            exceptionHook.onConstraintException(e);
        }
        catch (FailedCollectionException e)
        {
            exceptionHook.onCollectionException(e);
        }

        int total = session.completed() + session.remaining();
        double ratio = (double) session.completed() / (double) total;
        progress.setProgress(ratio * 100);
        Framework.info("[Collection] Completed: " + session.completed() + ". Remaining: " + session.remaining()
                            + ". Progress: " + progress.getProgress() + "%");
    }

    @Override
    public boolean isFinished()
    {
        return session.isFinished();
    }

    @Override
    public CollectionResult<T> getResult()
    {
        CollectionResult<T> result = new CollectionResult<T>();
        result.getData().addAll(collected);
        return result;
    }

}
