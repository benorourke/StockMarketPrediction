package net.benorourke.stocks.framework.thread.preprocessing;

import net.benorourke.stocks.framework.series.data.Data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BatchProcessor<T>
{
    private final int batchSize;
    private final Queue<T> remaining;
    private final Processor<T> processor;

    public BatchProcessor(int batchSize, List<T> list, Processor<T> processor)
    {
        this.batchSize = batchSize;
        this.remaining = new LinkedList<>(list);
        this.processor = processor;
    }

    public void nextBatch()
    {
        List<T> batch = new ArrayList<T>();
        int count = 0;
        while (!remaining.isEmpty() && count < batchSize)
        {
            batch.add(remaining.poll());
            count ++;
        }

        processor.process(batch);
    }

    public boolean isExhausted()
    {
        return remaining.isEmpty();
    }

    public interface Processor<T> {

        void process(List<T> batch);

    }

}
