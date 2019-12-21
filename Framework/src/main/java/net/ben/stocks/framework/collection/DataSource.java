package net.ben.stocks.framework.collection;

import net.ben.stocks.framework.data.Data;
import net.ben.stocks.framework.exception.FailedCollectionException;

public interface DataSource
{

    Data retrieveNext() throws FailedCollectionException;

}
