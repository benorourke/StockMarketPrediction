package net.benorourke.stocks.framework.series.data;

import net.benorourke.stocks.framework.series.data.impl.ProcessedDocument;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;
import net.benorourke.stocks.framework.util.DateUtil;
import net.benorourke.stocks.framework.util.Nullable;
import net.benorourke.stocks.framework.util.Tuple;

import java.util.*;

/**
 * A helper class for handling datasets.
 */
public class DatasetHelper
{

    private DatasetHelper() { }

    /**
     * Adds documents on Saturday / Sunday to the documents on Monday; stock exchanges aren't open on a weekend.
     *
     * @param data the data to scan
     * @return the cloned map with all data on weekends shifted to a monday
     */
    public static <T extends Data> Map<Date, List<T>> handleWeekends(Map<Date, List<T>> data)
    {
        Map<Date, List<T>> weekdaysOnly = new HashMap<>();

        @Nullable List<T> carryForward = null;
        for (Map.Entry<Date, List<T>> entry : data.entrySet())
        {
            Date date = entry.getKey();
            if (DateUtil.isWeekend(date))
            {
                if (carryForward == null) carryForward = new ArrayList<>();

                carryForward.addAll(entry.getValue());
            }
            else
            {
                List<T> documents = (carryForward == null)
                                            ? new ArrayList<>()
                                            : new ArrayList<>(carryForward);
                carryForward = null;
                documents.addAll(entry.getValue());
                weekdaysOnly.put(entry.getKey(), documents);
            }
        }

        return weekdaysOnly;
    }

    /**
     * Check a dataset for any missing datatypes
     *
     * @param data the data
     * @param types the types
     * @param <T> the type of data
     * @return
     */
    public static <T extends Data> Map<Date, List<DataType>> checkMissingDataTypes(
                                                                Map<Date, List<T>> data, DataType... types)
    {
        Map<Date, List<DataType>> map = new LinkedHashMap<>();
        for (Map.Entry<Date, List<T>> entry : data.entrySet())
        {
            List<DataType> missingTypes = new ArrayList<>();
            for (DataType type : types)
            {
                if (!DatasetHelper.containsType(entry.getValue(), type))
                    missingTypes.add(type);
            }

            if (!missingTypes.isEmpty())
                map.put(entry.getKey(), missingTypes);
        }
        return map;
    }

    public static <T extends Data> boolean containsType(List<T> data, DataType type)
    {
        for (T elem : data)
            if (elem.getType().equals(type))
                return true;

        return false;
    }

    /**
     * Combine two separate lists of data and map them onto days.
     *
     * @param data
     * @return
     */
    public static Map<Date, List<Data>> combine(Tuple<List<ProcessedDocument>, List<StockQuote>> data)
    {
        Map<Date, List<Data>> combined = new LinkedHashMap<>();
        combine(combined, data.getA());
        combine(combined, data.getB());
        return combined;
    }

    /**
     * Combine two separate lists of data and map them onto days.
     *
     * @param data
     * @return
     */
    private static <T extends Data> void combine(Map<Date, List<Data>> combined, List<T> data)
    {
        for (T elem : data)
        {
            Date date = DateUtil.getDayStart(elem.getDate());
            if (!combined.containsKey(date))
                combined.put(date, new ArrayList<>());

            combined.get(date).add(elem);
        }
    }

}
