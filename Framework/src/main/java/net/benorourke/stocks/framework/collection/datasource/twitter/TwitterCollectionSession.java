package net.benorourke.stocks.framework.collection.datasource.twitter;

import net.benorourke.stocks.framework.collection.Query;
import net.benorourke.stocks.framework.collection.session.APICollectionSession;
import net.benorourke.stocks.framework.collection.session.DailyCollectionSession;
import net.benorourke.stocks.framework.collection.session.filter.CollectionFilter;
import net.benorourke.stocks.framework.series.data.impl.Document;

public class TwitterCollectionSession extends APICollectionSession<Document>
{
    // Wrap the object here
    private final DailyCollectionSession<Document> dailySession;

    public TwitterCollectionSession(Query completeQuery, CollectionFilter<Document> collectionFilter)
    {
        super(collectionFilter);

        dailySession = new DailyCollectionSession<>(completeQuery, collectionFilter);
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
}
