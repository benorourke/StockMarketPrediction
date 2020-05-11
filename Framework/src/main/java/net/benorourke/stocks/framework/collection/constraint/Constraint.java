package net.benorourke.stocks.framework.collection.constraint;

import net.benorourke.stocks.framework.collection.Query;
import net.benorourke.stocks.framework.exception.ConstraintException;

/**
 * An interface that states a reason why a DataSource would not be able to collect based on a given Query.
 */
public interface Constraint
{

    /**
     * Check whether a Query is valid for the given constraint.
     *
     * @param query the Query to check for
     * @throws ConstraintException the Exception thrown if it's invalid
     */
    void checkValid(Query query) throws ConstraintException;

}
