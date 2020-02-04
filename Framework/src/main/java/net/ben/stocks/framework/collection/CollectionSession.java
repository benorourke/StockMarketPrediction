package net.ben.stocks.framework.collection;

/**
 * Allows a large Query to be compounded into smaller Queries in an iterative manor
 * so that more data can be queried.
 */
public interface CollectionSession
{

    boolean isFinished();

    Query nextQuery();

    int completed();

    int remaining();

}
