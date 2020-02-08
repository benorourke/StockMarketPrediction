package net.ben.stocks.framework.collection.datasource;

import net.ben.stocks.framework.collection.ConnectionResponse;
import net.ben.stocks.framework.collection.Query;
import net.ben.stocks.framework.collection.constraint.Constraint;
import net.ben.stocks.framework.collection.session.CollectionSession;
import net.ben.stocks.framework.exception.ConstraintException;
import net.ben.stocks.framework.series.data.Data;
import net.ben.stocks.framework.exception.FailedCollectionException;

public abstract class DataSource<T extends Data>
{

    public abstract Class<? extends T> getDataClass();

    public abstract Constraint[] getConstraints();

    public abstract CollectionSession<T> newSession(Query completeQuery);

    public abstract ConnectionResponse<T> retrieve(Query query) throws ConstraintException, FailedCollectionException;

    public void checkConstraints(final Query query) throws ConstraintException
    {
        for (Constraint constraint : getConstraints())
        {
            constraint.checkValid(query);
        }
    }

}
