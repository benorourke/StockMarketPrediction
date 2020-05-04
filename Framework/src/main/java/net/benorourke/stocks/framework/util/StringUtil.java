package net.benorourke.stocks.framework.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    /**
     * Numbers are treated as lowercase letters.
     *
     * Examples:
     * testValue -> [test, Value]   (formatWords=false)
     * testValue -> [Test, Value]   (formatWords=true)
     *
     * @param value
     * @param formatWords capitalise all first letters of words, leaving the rest lowercased
     * @return
     */
    public static List<String> camelCaseToWords(String value, boolean formatWords)
    {
        String[] split = value.split("(?<!^)(?=[A-Z])");
        if (formatWords)
        {
            List<String> formatted = new ArrayList<>();
            for (String word : split)
            {
                if (word.length() == 1)
                    formatted.add(word.toUpperCase());
                else
                    formatted.add(word.substring(0, 1).toUpperCase() + word.substring(1));
            }

            return formatted;
        }
        else
        {
            return Arrays.asList(split);
        }
    }

}
