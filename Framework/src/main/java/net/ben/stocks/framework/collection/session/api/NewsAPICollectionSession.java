package net.ben.stocks.framework.collection.session.api;

import net.ben.stocks.framework.collection.Query;
import net.ben.stocks.framework.exception.ConstraintException;
import net.ben.stocks.framework.exception.FailedCollectionException;

import java.util.Collection;

public class NewsAPICollectionSession implements APICollectionSession
{

    public NewsAPICollectionSession(Query completeQuery)
    {

    }

    @Override
    public void onCollected(Collection data)
    {

    }

    @Override
    public void onCollectionException(FailedCollectionException exception)
    {
        // TODO - Switch API keys if rate limit exceeded
    }

    @Override
    public void onConstraintException(ConstraintException exception)
    {
        // TODO
    }

    @Override
    public boolean isFinished()
    {
        return false;
    }

    @Override
    public Query nextQuery()
    {
        return null;
    }

    @Override
    public int completed()
    {
        return 0;
    }

    @Override
    public int remaining()
    {
        return 0;
    }

}
