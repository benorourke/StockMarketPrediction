package net.benorourke.stocks.framework.model.data;

import javafx.util.Pair;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.util.ArrayList;
import java.util.List;

public class ProcessedCorpus
{
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

}
