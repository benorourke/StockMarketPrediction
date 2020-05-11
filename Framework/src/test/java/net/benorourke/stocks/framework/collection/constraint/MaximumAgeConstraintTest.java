package net.benorourke.stocks.framework.collection.constraint;

import net.benorourke.stocks.framework.collection.Query;
import net.benorourke.stocks.framework.exception.ConstraintException;
import net.benorourke.stocks.framework.util.DateUtil;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MaximumAgeConstraintTest
{

    @Test (expected = Test.None.class)
    public void checkValid_QueryWithinBounds_ShouldNotThrow() throws ConstraintException
    {
        Date now = new Date();
        Date lastWeek = DateUtil.addHours(now, -24 * 7);

        Query query = new Query(lastWeek, now);
        MaximumAgeConstraint maximumAgeConstraint = new MaximumAgeConstraint(TimeUnit.DAYS.toMillis(14));

        maximumAgeConstraint.checkValid(query);
    }

    @Test (expected = Test.None.class)
    public void checkValid_QueryNotWithinBounds_ShouldNotThrow() throws ConstraintException
    {
        Date now = new Date();
        Date lastWeek = DateUtil.addHours(now, -24 * 7);

        Query query = new Query(lastWeek, now);
        MaximumAgeConstraint maximumAgeConstraint = new MaximumAgeConstraint(TimeUnit.DAYS.toMillis(5));

        maximumAgeConstraint.checkValid(query);
    }

}
