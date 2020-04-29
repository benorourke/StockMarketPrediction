package net.benorourke.stocks.framework.util;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StringUtil
{
    private static final DecimalFormat FORMAT = new DecimalFormat("#.##");

    private StringUtil() {}

    public static String formatDoubles(double[] array)
    {
        return "[".concat(Arrays.stream(array)
                                .mapToObj(elem -> FORMAT.format(elem))
                                .collect(Collectors.joining(", ")))
                  .concat("]");
    }

    public static String formatDouble(double value)
    {
        return FORMAT.format(value);
    }

}
