package net.benorourke.stocks.framework.collection.session;

import net.benorourke.stocks.framework.exception.ConstraintException;
import net.benorourke.stocks.framework.exception.FailedCollectionException;
import net.benorourke.stocks.framework.series.data.Data;

public interface APICollectionSession<T extends Data> extends CollectionSession<T>
{

    void onCollectionException(FailedCollectionException exception);

    void onConstraintException(ConstraintException exception);

}
