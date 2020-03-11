package net.benorourke.stocks.framework.collection.session;

import net.benorourke.stocks.framework.collection.Query;
import net.benorourke.stocks.framework.collection.session.filter.CollectionFilter;
import net.benorourke.stocks.framework.series.data.Data;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<T> filter(Collection<T> data)
    {
        return data.stream()
                   .filter(d -> !collectionFilter.discard(d))
                   .collect(Collectors.toList());
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
