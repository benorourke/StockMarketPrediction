package net.benorourke.stocks.framework.collection.constraint;

import net.benorourke.stocks.framework.collection.Query;
import net.benorourke.stocks.framework.exception.ConstraintException;
import net.benorourke.stocks.framework.util.DateUtil;
import org.junit.Test;

import java.util.Date;

public class OrderingConstraintTest
{
    private static final OrderingConstraint CONSTRAINT = new OrderingConstraint();

    @Test (expected = Test.None.class)
    public void checkValid_FromBeforeTo_ShouldNotThrow() throws ConstraintException
    {
        Date now = new Date();
        Date yesterday = DateUtil.addHours(now, -24);

        CONSTRAINT.checkValid(new Query(now, yesterday));
    }

    @Test (expected = ConstraintException.class)
    public void checkValid_FromAfterTo_ShouldThrow() throws ConstraintException
    {
        Date now = new Date();
        Date yesterday = DateUtil.addHours(now, -24);

        CONSTRAINT.checkValid(new Query(yesterday, now));
    }

}
