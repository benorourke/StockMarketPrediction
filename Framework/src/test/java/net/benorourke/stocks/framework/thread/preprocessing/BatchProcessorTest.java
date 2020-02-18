package net.benorourke.stocks.framework.thread.preprocessing;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BatchProcessorTest
{

    @Test
    public void consumeUntilExhausted_NElementsNBatchSize_ShouldReturn1Batch()
    {
        final int n = 13;

        List<Integer> elems = new ArrayList<Integer>();
        for (int i = 0; i < n; i ++)
            elems.add(i);

        BatchProcessor<Integer> generator = new BatchProcessor<>(n, elems,
                batch -> assertEquals(n, batch.size()));

        generator.nextBatch();
        assertEquals(true, generator.isExhausted());
    }

}
