package net.benorourke.stocks.framework.collection.datasource.twitter;

import net.benorourke.stocks.framework.collection.Query;
import net.benorourke.stocks.framework.collection.session.APICollectionSession;
import net.benorourke.stocks.framework.collection.session.DailyCollectionSession;
import net.benorourke.stocks.framework.collection.session.filter.CollectionFilter;
import net.benorourke.stocks.framework.exception.ConstraintException;
import net.benorourke.stocks.framework.exception.FailedCollectionException;
import net.benorourke.stocks.framework.series.data.impl.Document;
import twitter4j.Twitter;

public class Twitter4JCollectionSession extends APICollectionSession<Document>
{
    private final Twitter twitter;
    // Wrap the object here
    private final DailyCollectionSession<Document> dailySession;

    public Twitter4JCollectionSession(Twitter twitter, Query completeQuery, CollectionFilter<Document> collectionFilter)
    {
        super(collectionFilter);

        this.twitter = twitter;
        dailySession = new DailyCollectionSession<>(completeQuery, collectionFilter);
    }

    @Override
    public boolean isFinished()
    {
        return dailySession.isFinished();
    }

    @Override
    public Twitter4JQuery nextQuery()
    {
        Query next = dailySession.nextQuery();
        return new Twitter4JQuery(twitter, next.getTo(), next.getFrom());
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
        exception.printStackTrace();
    }

    @Override
    public void onConstraintException(ConstraintException exception)
    {
        exception.printStackTrace();
    }
}
