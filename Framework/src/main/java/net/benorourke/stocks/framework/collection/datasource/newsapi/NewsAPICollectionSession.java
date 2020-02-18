package net.benorourke.stocks.framework.collection.datasource.newsapi;

import net.benorourke.stocks.framework.collection.Query;
import net.benorourke.stocks.framework.collection.session.APICollectionSession;
import net.benorourke.stocks.framework.collection.session.DailyCollectionSession;
import net.benorourke.stocks.framework.exception.ConstraintException;
import net.benorourke.stocks.framework.exception.FailedCollectionException;
import net.benorourke.stocks.framework.series.data.impl.Document;

public class NewsAPICollectionSession implements APICollectionSession<Document>
{
    // Wrap the object here
    private DailyCollectionSession<Document> dailySession;

    public NewsAPICollectionSession(Query completeQuery)
    {
        dailySession = new DailyCollectionSession<>(completeQuery);
    }

    @Override
    public boolean isFinished()
    {
        return dailySession.isFinished();
    }

    @Override
    public Query nextQuery()
    {
        return dailySession.nextQuery();
    }

    @Override
    public int completed()
    {
        return dailySession.completed();
    }

    @Override
    public int remaining()
    {
        return dailySession.remaining();
    }

    @Override
    public void onCollectionException(FailedCollectionException exception)
    {
        // TODO - Switch API keys if rate limit exceeded
        exception.printStackTrace();
    }

    @Override
    public void onConstraintException(ConstraintException exception)
    {
        exception.printStackTrace();
    }

}