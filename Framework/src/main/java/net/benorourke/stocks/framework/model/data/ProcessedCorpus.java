package net.benorourke.stocks.framework.model.data;

import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.util.ArrayList;
import java.util.List;

public class ProcessedCorpus implements DataSetIterator
{
    private static final int COUNT_FEATURES = 5;
    private static final int COUNT_LABELS = 5;

    private final List<ModelData> data;

    public ProcessedCorpus(List<ModelData> data)
    {
        this.data = data;
    }

    public ProcessedCorpus()
    {
        this(new ArrayList<>());
    }

    public List<ModelData> getData()
    {
        return data;
    }

    public int size()
    {
        return data.size();
    }

    @Override
    public DataSet next(int i)
    {
        return null;
    }

    @Override
    public int totalExamples()
    {
        return data.size();
    }

    @Override
    public int inputColumns()
    {
        return COUNT_FEATURES;
    }

    @Override
    public int totalOutcomes()
    {
        return COUNT_LABELS;
    }

    @Override
    public boolean resetSupported()
    {
        return false;
    }

    @Override
    public boolean asyncSupported()
    {
        return false;
    }

    @Override
    public void reset()
    {

    }

    @Override
    public int batch()
    {
        return 0;
    }

    @Override
    public int cursor()
    {
        return 0;
    }

    @Override
    public int numExamples()
    {
        return 0;
    }

    @Override
    public void setPreProcessor(DataSetPreProcessor dataSetPreProcessor)
    {

    }

    @Override
    public DataSetPreProcessor getPreProcessor()
    {
        return null;
    }

    @Override
    public List<String> getLabels()
    {
        return null;
    }

    @Override
    public boolean hasNext()
    {
        return false;
    }

    @Override
    public DataSet next()
    {
        return null;
    }

}
