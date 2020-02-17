package net.benorourke.stocks.framework.collection.constraint;

import net.benorourke.stocks.framework.collection.Query;
import net.benorourke.stocks.framework.exception.ConstraintException;

public class OrderingConstraint implements Constraint
{

    @Override
    public void checkValid(Query query) throws ConstraintException
    {
        if(query.getTo().before(query.getFrom()))
            throw new ConstraintException("Query to cannot be before from");
    }

}
