package net.ben.stocks.framework.collection.constraint;

import net.ben.stocks.framework.collection.Query;
import net.ben.stocks.framework.exception.ConstraintException;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MaximumAgeConstraint implements Constraint
{
    private long maximumTimeMs;

    /**
     * Constructor
     * @param maximumTimeMs
     */
    public MaximumAgeConstraint(long maximumTimeMs)
    {
        this.maximumTimeMs = maximumTimeMs;
    }

    public MaximumAgeConstraint(int maximumTimeDays)
    {
        this(TimeUnit.DAYS.toMillis(maximumTimeDays));
    }

    @Override
    public void checkValid(Query query) throws ConstraintException
    {
        if(query.getFrom().before(getMaximumDate()))
            throw new ConstraintException("Date-range exceeds minimum date allowed by ");
    }

    /**
     * Dynamically create a Date as we want to get the maximum Date relative to the current.
     *
     * @return
     */
    private Date getMaximumDate()
    {
        return new Date(System.currentTimeMillis() - maximumTimeMs);
    }

}