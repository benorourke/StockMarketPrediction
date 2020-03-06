package net.benorourke.stocks.framework.collection.datasource.alphavantage;

import net.benorourke.stocks.framework.collection.Query;
import net.benorourke.stocks.framework.collection.session.APICollectionSession;
import net.benorourke.stocks.framework.exception.ConstraintException;
import net.benorourke.stocks.framework.exception.FailedCollectionException;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;

/**
 * Session only runs once since AlphaVantage returns enough feedforward in one API call.
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
