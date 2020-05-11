package net.benorourke.stocks.framework.collection.session;

import net.benorourke.stocks.framework.collection.Query;
import net.benorourke.stocks.framework.collection.session.filter.CollectionFilter;
import net.benorourke.stocks.framework.series.data.Data;

/**
 * Allows a large Query to be compounded into smaller Queries in an iterative manor
 * so that more data can be retrieved.
 */
public abstract class CollectionSession<T extends Data>
{
    private final CollectionFilter<T> collectionFilter;

    public CollectionSession(CollectionFilter<T> collectionFilter)
    {
        this.collectionFilter = collectionFilter;
    }

    /**
     * Whether all of the queries have been used.
     * @return
     */
    public abstract boolean isFinished();

    /**
     * Get the next query if {@link #isFinished()} is false
     * @return the query
     */
    public abstract Query nextQuery();

    /**
     * Total number of queries used so far
     * @return
     */
    public abstract int completed();

    /**
     * Total number of queries remaining
     * @return
     */
    public abstract int remaining();

    public CollectionFilter getCollectionFilter()
    {
        return collectionFilter;
    }

}
