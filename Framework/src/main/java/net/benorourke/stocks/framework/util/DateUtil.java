package net.benorourke.stocks.framework.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil
{
    private static final DateFormat SIMPLE_UK_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private static final DateFormat DETAILED_UK_DATE_FORMAT = new SimpleDateFormat("dd/MM/YYYY hh:mm:ss");

    private DateUtil() {}

    public static String formatSimple(Date date)
    {
        return SIMPLE_UK_DATE_FORMAT.format(date);
    }

    public static String formatDetailed(Date date)
    {
        return DETAILED_UK_DATE_FORMAT.format(date);
    }

    public static Date parseDetailedUK(String strDate)
    {
        try
        {
            return DETAILED_UK_DATE_FORMAT.parse(strDate);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean sameDay(Date a, Date b)
    {
        Calendar calA = Calendar.getInstance();
        Calendar calB = Calendar.getInstance();
        calA.setTime(a);
        calB.setTime(b);

        return calA.get(Calendar.DAY_OF_YEAR) == calB.get(Calendar.DAY_OF_YEAR)
                    && calA.get(Calendar.YEAR) == calB.get(Calendar.YEAR);
    }

    /**
     * Get the time at 00:01 (midnight) on any day.
     *
     * @param date the date of the day to convert to 1-past midnight
     * @return 1-past midnight on the specified day / {@link java.util.Date}
     */
    public static Date getDayStart(Date date)
    {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 1);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

}
