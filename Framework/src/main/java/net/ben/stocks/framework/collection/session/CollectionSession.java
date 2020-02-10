package net.ben.stocks.framework.collection.session;

import net.ben.stocks.framework.collection.Query;
import net.ben.stocks.framework.series.data.Data;

/**
 * Allows a large Query to be compounded into smaller Queries in an iterative manor
 * so that more data can be queried.
 */
public interface CollectionSession<T extends Data>
{

    boolean isFinished();

    Query nextQuery();

    int completed();

    int remaining();

}
