package net.benorourke.stocks.framework.series.data;

import net.benorourke.stocks.framework.series.data.impl.CleanedDocument;
import net.benorourke.stocks.framework.util.DateUtil;
import net.benorourke.stocks.framework.util.Nullable;

import java.util.*;

public class DatasetHelper
{

    private DatasetHelper() { }

    /**
     * Adds documents on Saturday / Sunday to the documents on Monday; stock exchanges aren't open on a weekend.
     * @param data
     * @return
     */
    public static Map<Date, List<CleanedDocument>> handleWeekends(Map<Date, List<CleanedDocument>> data)
    {
        Map<Date, List<CleanedDocument>> weekdaysOnly = new HashMap<>();

        @Nullable List<CleanedDocument> carryForward = null;
        for (Map.Entry<Date, List<CleanedDocument>> entry : data.entrySet())
        {
            Date date = entry.getKey();
            if (DateUtil.isWeekend(date))
            {
                if (carryForward == null) carryForward = new ArrayList<>();

                carryForward.addAll(entry.getValue());
            }
            else
            {
                List<CleanedDocument> documents = (carryForward == null)
                                                        ? new ArrayList<>()
                                                        : new ArrayList<>(carryForward);
                carryForward = null;
                documents.addAll(entry.getValue());
                weekdaysOnly.put(entry.getKey(), documents);
            }
        }

        return weekdaysOnly;
    }

}
