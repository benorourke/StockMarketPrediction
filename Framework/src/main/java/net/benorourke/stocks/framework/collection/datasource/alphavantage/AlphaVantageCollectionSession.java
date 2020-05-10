package net.benorourke.stocks.framework.collection.datasource.alphavantage;

import net.benorourke.stocks.framework.collection.Query;
import net.benorourke.stocks.framework.collection.session.APICollectionSession;
import net.benorourke.stocks.framework.collection.session.filter.CollectionFilter;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;

/**
 * Session only runs once since AlphaVantage returns enough feedforward in one API call.
 *
 * TODO - API key cycling
 */
public class AlphaVantageCollectionSession extends APICollectionSession<StockQuote>
{
    private final Query completeQuery;
    private boolean done;

    protected AlphaVantageCollectionSession(Query completeQuery, CollectionFilter<StockQuote> collectionFilter)
    {
        super(collectionFilter);

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

}
