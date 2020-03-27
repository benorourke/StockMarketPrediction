package net.benorourke.stocks.framework.collection.datasource;

import net.benorourke.stocks.framework.collection.ConnectionResponse;
import net.benorourke.stocks.framework.collection.Query;
import net.benorourke.stocks.framework.collection.constraint.Constraint;
import net.benorourke.stocks.framework.collection.session.APICollectionSession;
import net.benorourke.stocks.framework.collection.session.filter.CollectionFilter;
import net.benorourke.stocks.framework.exception.ConstraintException;
import net.benorourke.stocks.framework.exception.FailedCollectionException;
import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.DataType;

public abstract class DataSource<T extends Data>
{
    private final String apiKey;

    public DataSource(String apiKey)
    {
        this.apiKey = apiKey;
    }

    public abstract Class<? extends T> getDataClass();

    public abstract DataType getDataType();

    public abstract Constraint[] getConstraints();

    public abstract APICollectionSession<T> newSession(Query completeQuery, CollectionFilter<T> collectionFilter);

    public abstract ConnectionResponse<T> retrieve(Query query) throws ConstraintException, FailedCollectionException;

    public void checkConstraints(final Query query) throws ConstraintException
    {
        for (Constraint constraint : getConstraints())
        {
            constraint.checkValid(query);
        }
    }

    public String getApiKey()
    {
        return apiKey;
    }

}
