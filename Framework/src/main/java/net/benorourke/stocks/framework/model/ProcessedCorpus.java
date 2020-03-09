package net.benorourke.stocks.framework.model;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProcessedCorpus implements Iterable<ModelData>
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
    public Iterator<ModelData> iterator()
    {
        return data.iterator();
    }

    /**
     *
     * @param splitRatio 0.6 would mean 60% in the first array, 40% in the second
     * @return
     */
    public List<ProcessedCorpus> split(double splitRatio)
    {
        int cardinality = size();
        int index = (int) Math.round(cardinality * splitRatio);

        List<ProcessedCorpus> datasets = new ArrayList<>();
        datasets.add(new ProcessedCorpus(data.subList(0, index)));
        datasets.add(new ProcessedCorpus(data.subList(index + 1, cardinality - 1)));
        return datasets;
    }

    public DataSet toDataSet(long seed)
    {
        int size = size();

        double[][] inputsMatrix = new double[size][COUNT_FEATURES];
        double[][] outputsMatrix = new double[size][COUNT_LABELS];

        int row = 0;
        for (ModelData data : this.data)
        {
            for (int col = 0; col < COUNT_FEATURES; col ++)
            {
                inputsMatrix[row] = data.getFeatures();
                outputsMatrix[row] = data.getLabels();
            }

            row ++;
        }

        INDArray inputs = Nd4j.create(inputsMatrix);
        INDArray outputs = Nd4j.create(outputsMatrix);
        DataSet set = new DataSet(inputs, outputs);

        set.shuffle(seed);
        return set;
    }

}
