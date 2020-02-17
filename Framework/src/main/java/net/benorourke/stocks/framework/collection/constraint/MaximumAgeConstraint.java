package net.benorourke.stocks.framework.collection.constraint;

import net.benorourke.stocks.framework.collection.Query;
import net.benorourke.stocks.framework.exception.ConstraintException;

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
            throw new ConstraintException("Date-range exceeds minimum date allowed.");
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
