package net.ben.stocks.framework.thread.impl.collection;

import net.ben.stocks.framework.Framework;
import net.ben.stocks.framework.collection.CollectionSession;
import net.ben.stocks.framework.collection.DataSource;
import net.ben.stocks.framework.collection.Query;
import net.ben.stocks.framework.exception.ConstraintException;
import net.ben.stocks.framework.exception.FailedCollectionException;
import net.ben.stocks.framework.series.data.Data;
import net.ben.stocks.framework.thread.Task;
import net.ben.stocks.framework.thread.Progress;

import java.util.Collection;
import java.util.Random;

public class CollectionTask<T extends Data> implements Task<CollectionResult>
{
    private final DataSource<T> dataSource;
    private final CollectionSession session;
    /**
     * Elements are appended to the list as collected
     */
    private final CollectionResult<T> result;

    private Progress progress;

    public CollectionTask(DataSource<T> dataSource, CollectionSession session)
    {
        this.dataSource = dataSource;
        this.session = session;
        result = new CollectionResult<T>();
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
            Collection<T> retrieved = dataSource.retrieve(next);
            result.getData().addAll(retrieved);

            Framework.info("Collected " + retrieved.size() + " data for " + next.toString());
        }
        catch (ConstraintException e)
        {
            e.printStackTrace();
        }
        catch (FailedCollectionException e)
        {
            e.printStackTrace();
        }

        int total = session.completed() + session.remaining();
        double ratio = (double) total / (double) session.completed();
        progress.setProgress(ratio * 100);
    }

    @Override
    public boolean isFinished()
    {
        return session.isFinished();
    }

    @Override
    public CollectionResult getResult()
    {
        return result;
    }

}
