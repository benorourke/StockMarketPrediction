package net.benorourke.stocks.framework.thread.collection;

import net.benorourke.stocks.framework.exception.ConstraintException;
import net.benorourke.stocks.framework.exception.FailedCollectionException;

/**
 * The hook for collection-based exceptions.
 */
public interface CollectionExceptionHook
{

    void onCollectionException(FailedCollectionException exception);

    void onConstraintException(ConstraintException exception);

}
