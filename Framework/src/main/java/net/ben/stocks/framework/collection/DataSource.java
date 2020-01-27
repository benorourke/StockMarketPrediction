package net.ben.stocks.framework.collection;

import net.ben.stocks.framework.series.data.Data;
import net.ben.stocks.framework.exception.FailedCollectionException;

import java.util.Collection;

public abstract class DataSource<T extends Data>
{

    public abstract Class<? extends T> getDataClass();

    public abstract Constraint[] getConstraints();

    public abstract Collection<T> retrieveNext(Query query) throws FailedCollectionException;

}
