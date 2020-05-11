package net.benorourke.stocks.framework.net.benorourke.stocks.framework.model;

import net.benorourke.stocks.framework.model.ModelData;
import net.benorourke.stocks.framework.model.ProcessedDataset;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ProcessedDatasetTest
{

    @Test
    public void split_CardinalityTwo_ShouldSplitEvenly()
    {
        List<ModelData> data = new ArrayList<>();
        for (int i = 0; i < 2; i ++)
        {
            double[] array = new double[10];
            Arrays.fill(array, i);

            data.add(new ModelData(new Date(), array, array));
        }
        ProcessedDataset set = new ProcessedDataset(new ArrayList<>(), new ArrayList<>(), 10, 10, data);

        // Split halfway
        List<ProcessedDataset> split = set.split(0.5);
        assertEquals(1, split.get(0).size());
        assertEquals(1, split.get(1).size());
    }

    @Test
    public void split_CardinalityTen_ShouldSplitEvenly()
    {
        List<ModelData> data = new ArrayList<>();
        for (int i = 0; i < 10; i ++)
        {
            double[] array = new double[10];
            Arrays.fill(array, i);

            data.add(new ModelData(new Date(), array, array));
        }
        ProcessedDataset set = new ProcessedDataset(new ArrayList<>(), new ArrayList<>(), 10, 10, data);

        // Split halfway
        List<ProcessedDataset> split = set.split(0.5);
        assertEquals(5, split.get(0).size());
        assertEquals(5, split.get(1).size());
    }


}
