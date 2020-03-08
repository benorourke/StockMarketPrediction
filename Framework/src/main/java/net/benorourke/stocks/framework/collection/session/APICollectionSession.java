package net.benorourke.stocks.framework.collection.session;

import net.benorourke.stocks.framework.collection.session.filter.CollectionFilter;
import net.benorourke.stocks.framework.exception.ConstraintException;
import net.benorourke.stocks.framework.exception.FailedCollectionException;
import net.benorourke.stocks.framework.series.data.Data;

public abstract class APICollectionSession<T extends Data> extends CollectionSession<T>
{

    public APICollectionSession(CollectionFilter<T> collectionFilter)
    {
        super(collectionFilter);
    }

    public abstract void onCollectionException(FailedCollectionException exception);

    public abstract void onConstraintException(ConstraintException exception);

}
