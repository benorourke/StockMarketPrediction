package net.benorourke.stocks.framework.collection.session;

import net.benorourke.stocks.framework.collection.Query;
import net.benorourke.stocks.framework.collection.session.filter.CollectionFilter;
import net.benorourke.stocks.framework.series.data.Data;

/**
 * Allows a large Query to be compounded into smaller Queries in an iterative manor
 * so that more feedforward can be queried.
 */
public abstract class CollectionSession<T extends Data>
{
    private final CollectionFilter<T> collectionFilter;

    public CollectionSession(CollectionFilter<T> collectionFilter)
    {
        this.collectionFilter = collectionFilter;
    }

    public abstract boolean isFinished();

    public abstract Query nextQuery();

    public abstract int completed();

    public abstract int remaining();

    public CollectionFilter getCollectionFilter()
    {
        return collectionFilter;
    }

}
