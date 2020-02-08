package net.ben.stocks.framework.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil
{

    private DateUtil() {}

    public static boolean sameDay(Date a, Date b)
    {
        Calendar calA = Calendar.getInstance();
        Calendar calB = Calendar.getInstance();
        calA.setTime(a);
        calB.setTime(b);

        return calA.get(Calendar.DAY_OF_YEAR) == calB.get(Calendar.DAY_OF_YEAR)
                    && calA.get(Calendar.YEAR) == calB.get(Calendar.YEAR);
    }

}
