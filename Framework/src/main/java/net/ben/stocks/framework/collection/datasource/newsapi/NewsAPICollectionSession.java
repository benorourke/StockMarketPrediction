package net.ben.stocks.framework.collection.datasource.newsapi;

import net.ben.stocks.framework.collection.Query;
import net.ben.stocks.framework.collection.session.APICollectionSession;
import net.ben.stocks.framework.collection.session.DailyCollectionSession;
import net.ben.stocks.framework.exception.ConstraintException;
import net.ben.stocks.framework.exception.FailedCollectionException;
import net.ben.stocks.framework.series.data.Document;

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
