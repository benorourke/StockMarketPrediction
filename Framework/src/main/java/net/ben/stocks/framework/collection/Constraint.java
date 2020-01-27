package net.ben.stocks.framework.collection;

import net.ben.stocks.framework.exception.ConstraintException;

public interface Constraint
{

    /**
     * Check whether a Query is valid for the given constraint.
     *
     * @param query the Query to check for
     * @throws ConstraintException the Exception thrown if it's invalid
     */
    void checkValid(Query query) throws ConstraintException;

    String getExceptionMessage();

}
