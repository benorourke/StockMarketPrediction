package net.ben.stocks.framework.collection.datasource.alphavantage;

import net.ben.stocks.framework.Framework;
import net.ben.stocks.framework.collection.Query;
import net.ben.stocks.framework.collection.session.APICollectionSession;
import net.ben.stocks.framework.exception.ConstraintException;
import net.ben.stocks.framework.exception.FailedCollectionException;
import net.ben.stocks.framework.series.data.StockQuote;

/**
 * Session only runs once since AlphaVantage returns enough data in one API call.
 *
 * TODO - API key cycling
 */
public class AlphaVantageCollectionSession implements APICollectionSession<StockQuote>
{
    private final Query completeQuery;
    private boolean done;

    public AlphaVantageCollectionSession(Query completeQuery)
    {
        this.completeQuery = completeQuery;
        done = false;
    }

    @Override
    public boolean isFinished()
    {
        return done;
    }

    @Override
    public Query nextQuery()
    {
        done = true;
        return completeQuery;
    }

    @Override
    public int completed()
    {
        return done ? 1 : 0;
    }

    @Override
    public int remaining()
    {
        return done ? 0 : 1;
    }

    @Override
    public void onCollectionException(FailedCollectionException exception)
    {
        // TODO
    }

    @Override
    public void onConstraintException(ConstraintException exception)
    {
        // TODO
    }

}
