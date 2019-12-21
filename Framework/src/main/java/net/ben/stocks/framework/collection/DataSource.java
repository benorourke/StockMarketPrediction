package net.ben.stocks.framework.collection;

import net.ben.stocks.framework.data.Data;
import net.ben.stocks.framework.exception.FailedCollectionException;

import java.util.Collection;

public interface DataSource<T extends Data>
{

    Class<? extends T> getDataClazz();

    Collection<T> retrieveNext(Query query) throws FailedCollectionException;

}
