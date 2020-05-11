package net.benorourke.stocks.framework.collection.constraint;

import net.benorourke.stocks.framework.collection.Query;
import net.benorourke.stocks.framework.exception.ConstraintException;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * A Constraint that states that the from date of the Query must be before a given date relative to the current time.
 */
public class MaximumAgeConstraint implements Constraint
{
    private final long maximumTimeMs;

    /**
     * Create a new instance.
     *
     * @param maximumTimeMs the maximum time, in milliseconds, from the current date that the from date of a query
     *                      can be
     */
    public MaximumAgeConstraint(long maximumTimeMs)
    {
        this.maximumTimeMs = maximumTimeMs;
    }

    /**
     * Create a new instance.
     *
     * @param maximumTimeDays the maximum time, in days, from the current date that the from date of a query can be
     */
    public MaximumAgeConstraint(int maximumTimeDays)
    {
        this(TimeUnit.DAYS.toMillis(maximumTimeDays));
    }

    @Override
    public void checkValid(Query query) throws ConstraintException
    {
        if(query.getFrom().before(getMaximumDate()))
            throw new ConstraintException("Date-range exceeds oldest date(s) allowed.");
    }

    /**
     * Dynamically create a Date relative to the current time based on the maximum amount of allowed time.
     *
     * @return the furthest away Date that will not throw a ConstraintException
     */
    private Date getMaximumDate()
    {
        return new Date(System.currentTimeMillis() - maximumTimeMs);
    }

}
