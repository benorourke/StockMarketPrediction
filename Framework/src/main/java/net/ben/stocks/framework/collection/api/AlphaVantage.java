package net.ben.stocks.framework.collection.api;

import net.ben.stocks.framework.collection.session.CollectionSession;
import net.ben.stocks.framework.collection.constraint.Constraint;
import net.ben.stocks.framework.collection.DataSource;
import net.ben.stocks.framework.collection.Query;
import net.ben.stocks.framework.collection.constraint.OrderingConstraint;
import net.ben.stocks.framework.exception.ConstraintException;
import net.ben.stocks.framework.series.data.StockQuote;
import net.ben.stocks.framework.exception.FailedCollectionException;

import java.util.Collection;

public class AlphaVantage extends DataSource<StockQuote>
{
    private static final String BASE_URL = "https://www.alphavantage.co/";

    private final String apiKey;

    public AlphaVantage(String apiKey)
    {
        this.apiKey = apiKey;
    }

    @Override
    public Class<? extends StockQuote> getDataClass()
    {
        return StockQuote.class;
    }

    @Override
    public Constraint[] getConstraints()
    {
        return new Constraint[]
                {
                        new OrderingConstraint()
                };
    }

    @Override
    public CollectionSession newSession(Query completeQuery)
    {
        return null;
    }

    @Override
    public Collection<StockQuote> retrieve(Query query) throws FailedCollectionException, ConstraintException
    {
        checkConstraints(query);

        return null;
    }

}
