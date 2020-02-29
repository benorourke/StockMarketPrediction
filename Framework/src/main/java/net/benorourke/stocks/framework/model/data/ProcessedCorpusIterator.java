package net.benorourke.stocks.framework.model.data;

import javafx.util.Pair;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ProcessedCorpusIterator implements DataSetIterator
{
    /** Size is the number of features for any given datapoint. */
    public static final int INPUT_VECTOR_SIZE = 5;

    private final ProcessedCorpus corpus;
    private final int batchSize;

    private List<ModelData> trainDataset;
    private List<Pair<INDArray, INDArray>> testDataset;

    /**
     *
     * @param corpus
     * @param batchSize
     * @param split the ratio in the range of [0,1] for train:test dataset sizes
     */
    public ProcessedCorpusIterator(ProcessedCorpus corpus, int batchSize, double split)
    {
        this.corpus = corpus;
        this.batchSize = batchSize;

        int splitAt = (int) Math.round(corpus.size() * split);
        trainDataset = corpus.getData().subList(0, splitAt);
//        testDataset = generateTestDataSet(corpus.getData().subList(splitAt, corpus.size()));
    }

    @Override
    public DataSet next(int i)
    {
        return null;
    }

    @Override
    public int inputColumns()
    {
        return 0;
    }

    @Override
    public int totalOutcomes()
    {
        return 0;
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
