package net.benorourke.stocks.framework.thread;

import org.junit.Test;

import java.util.LinkedHashMap;

import static org.junit.Assert.assertTrue;

public class ProgressTest
{

    @Test
    public void getBounds_SumEquals100_ShouldIncreaseLinearly()
    {
        LinkedHashMap<Integer, Double> mappings = new LinkedHashMap<>();
        mappings.put(0, 20.0);
        mappings.put(1, 50.0);
        mappings.put(2, 30.0);

        Progress progress = new Progress();
        Progress.Helper helper = new Progress.Helper(progress, mappings);

        assertTrue(helper.getBounds(0).getA() == 0);
        assertTrue(helper.getBounds(0).getB() == 20);

        assertTrue(helper.getBounds(1).getA() == 20);
        assertTrue(helper.getBounds(1).getB() == 70);

        assertTrue(helper.getBounds(2).getA() == 70);
        assertTrue(helper.getBounds(2).getB() == 100);
    }

}
