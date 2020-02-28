package net.benorourke.stocks.framework.series.data;

import net.benorourke.stocks.framework.series.data.impl.CleanedDocument;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class DatasetHelperTest
{

    @Test
    public void handleWeekends_ContainsWeekend_ShouldCompress()
    {
        Map<Date, List<CleanedDocument>> dataset = new HashMap<>();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2020);
        cal.set(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        for (int i = 0; i < 21; i ++) // Three weeks; should compress at least 6 days
        {
            Date date = new Date(cal.getTime().getTime());
            dataset.put(date, new ArrayList<>());
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        Map<Date, List<CleanedDocument>> compressed = DatasetHelper.handleWeekends(dataset);
        assertEquals(true, compressed.size() < dataset.size());
        int delta = dataset.size() - compressed.size();
        assertEquals(true, delta >= 5);
    }

}
