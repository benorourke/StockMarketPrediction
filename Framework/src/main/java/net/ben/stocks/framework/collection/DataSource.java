package net.ben.stocks.framework.collection;

import net.ben.stocks.framework.collection.constraint.Constraint;
import net.ben.stocks.framework.exception.ConstraintException;
import net.ben.stocks.framework.series.data.Data;
import net.ben.stocks.framework.exception.FailedCollectionException;

import java.util.Collection;

public abstract class DataSource<T extends Data>
{

    public abstract Class<? extends T> getDataClass();

    public abstract Constraint[] getConstraints();

    public abstract Collection<T> retrieve(Query query) throws ConstraintException, FailedCollectionException;

    public void checkConstraints(final Query query) throws ConstraintException
    {
        for (Constraint constraint : getConstraints())
        {
            constraint.checkValid(query);
        }
    }

}
