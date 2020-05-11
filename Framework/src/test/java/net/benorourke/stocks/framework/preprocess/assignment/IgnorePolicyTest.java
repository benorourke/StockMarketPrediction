package net.benorourke.stocks.framework.preprocess.assignment;

import net.benorourke.stocks.framework.preprocess.assignment.IgnorePolicy;
import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.DataType;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class IgnorePolicyTest
{
    private final static IgnorePolicy POLICY = new IgnorePolicy();

    @Test
    public void handle_NothingMissing_ShouldRetainCardinality()
    {
        Map<Date, List<Data>> data = new HashMap<>();
        long now = System.currentTimeMillis();
        for (int i = 0; i < 10; i ++)
        {
            Date date = new Date(now + (i * 100000));
            data.put(date, new ArrayList<>());
        }

        int sizeBefore = data.size();
        POLICY.handle(data, new HashMap<>());
        assertEquals(sizeBefore, data.size());
    }

    @Test
    public void handle_AllMissing_ShouldBecomeEmpty()
    {
        Map<Date, List<Data>> data = new HashMap<>();
        Map<Date, List<DataType>> missingTypes = new HashMap<>();
        long now = System.currentTimeMillis();
        for (int i = 0; i < 10; i ++)
        {
            Date date = new Date(now + (i * 100000));
            data.put(date, new ArrayList<>());
            missingTypes.put(date, new ArrayList<>());
        }

        POLICY.handle(data, missingTypes);
        assertEquals(0, data.size());
    }

}
