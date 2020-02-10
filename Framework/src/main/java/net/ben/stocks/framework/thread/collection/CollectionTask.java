package net.ben.stocks.framework.thread.collection;

import net.ben.stocks.framework.Framework;
import net.ben.stocks.framework.collection.ConnectionResponse;
import net.ben.stocks.framework.collection.session.APICollectionSession;
import net.ben.stocks.framework.collection.datasource.DataSource;
import net.ben.stocks.framework.collection.Query;
import net.ben.stocks.framework.exception.ConstraintException;
import net.ben.stocks.framework.exception.FailedCollectionException;
import net.ben.stocks.framework.series.data.Data;
import net.ben.stocks.framework.thread.Task;
import net.ben.stocks.framework.thread.Progress;

public class CollectionTask<T extends Data> implements Task<CollectionResult>
{
    private final DataSource<T> dataSource;
    private final APICollectionSession session;
    /**
     * Elements are appended to the list as collected
     */
    private final CollectionResult<T> result;

    private Progress progress;

    public CollectionTask(DataSource<T> dataSource, APICollectionSession session)
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
            ConnectionResponse<T> response = dataSource.retrieve(next);
            result.getData().addAll(response.getData());
            Framework.info("Collected " + response.getData().size() + " data for " + next.toString());
        }
        catch (ConstraintException e)
        {
            session.onConstraintException(e);
        }
        catch (FailedCollectionException e)
        {
            session.onCollectionException(e);
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
