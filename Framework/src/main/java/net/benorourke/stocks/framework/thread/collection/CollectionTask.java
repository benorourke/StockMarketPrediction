package net.benorourke.stocks.framework.thread.collection;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.collection.ConnectionResponse;
import net.benorourke.stocks.framework.collection.session.APICollectionSession;
import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.collection.Query;
import net.benorourke.stocks.framework.exception.ConstraintException;
import net.benorourke.stocks.framework.exception.FailedCollectionException;
import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.thread.Task;
import net.benorourke.stocks.framework.thread.Progress;
import net.benorourke.stocks.framework.thread.TaskType;

public class CollectionTask<T extends Data> implements Task<CollectionDescription, CollectionResult<T>>
{
    private final DataSource<T> dataSource;
    private final APICollectionSession<T> session;
    /**
     * Elements are appended to the list as collected
     */
    private final CollectionResult<T> result;

    private Progress progress;

    public CollectionTask(DataSource<T> dataSource, APICollectionSession<T> session)
    {
        this.dataSource = dataSource;
        this.session = session;
        result = new CollectionResult<T>();
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
            ConnectionResponse<T> response = dataSource.retrieve(next);
            result.getData().addAll(response.getData());
            Framework.info("Collected " + response.getData().size() + " feedforward for " + next.toString());
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
        Framework.debug("Finished: " + session.isFinished());
        return session.isFinished();
    }

    @Override
    public CollectionResult getResult()
    {
        return result;
    }

}
