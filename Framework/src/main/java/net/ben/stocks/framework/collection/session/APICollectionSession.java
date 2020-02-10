package net.ben.stocks.framework.collection.session;

import net.ben.stocks.framework.collection.ConnectionResponse;
import net.ben.stocks.framework.collection.session.CollectionSession;
import net.ben.stocks.framework.exception.ConstraintException;
import net.ben.stocks.framework.exception.FailedCollectionException;
import net.ben.stocks.framework.series.data.Data;

import java.util.Collection;

public interface APICollectionSession<T extends Data> extends CollectionSession<T>
{

    void onCollectionException(FailedCollectionException exception);

    void onConstraintException(ConstraintException exception);

}
