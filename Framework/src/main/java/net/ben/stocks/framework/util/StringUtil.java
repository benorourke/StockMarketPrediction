package net.ben.stocks.framework.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtil
{
    private static final DateFormat UK_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    private StringUtil() {}

    public static String formatDate(Date date)
    {
        return UK_DATE_FORMAT.format(date);
    }

}
