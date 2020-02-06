package net.ben.stocks.framework.collection.session.api;

import net.ben.stocks.framework.collection.session.CollectionSession;
import net.ben.stocks.framework.exception.ConstraintException;
import net.ben.stocks.framework.exception.FailedCollectionException;
import net.ben.stocks.framework.series.data.Data;

import java.util.Collection;

public interface APICollectionSession<T extends Data> extends CollectionSession<T>
{

    /**
     * The function to be called when a Query from {@link#nextQuery()} is retrieved and used.
     *
     * This is used for pagination, etc. within the CollectionSession/
     *
     * @param data the collection of data that was collected
     */
    void onCollected(Collection<T> data);

    void onCollectionException(FailedCollectionException exception);

    void onConstraintException(ConstraintException exception);

}
