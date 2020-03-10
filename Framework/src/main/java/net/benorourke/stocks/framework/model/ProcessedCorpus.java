package net.benorourke.stocks.framework.model;

import net.benorourke.stocks.framework.series.data.impl.StockQuote;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

import java.util.*;

public class ProcessedCorpus implements Iterable<ModelData>
{

    private final String[] topTerms;
    private final List<ModelData> data;

    public ProcessedCorpus(String[] topTerms, List<ModelData> data)
    {
        this.topTerms = topTerms;
        this.data = data;
    }

    public ProcessedCorpus(String[] topTerms)
    {
        this(topTerms, new ArrayList<>());
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
     * Normalise the features based on the minimums & maximums provided.
     * @param featureMinimums
     * @param featureMaximums
     */
    public void normalise(double[] featureMinimums, double[] featureMaximums)
    {
        for (ModelData modelData : data)
        {
            for (int i = 0; i < ModelData.N_FEATURES; i ++)
            {
                double unnormalised = modelData.getFeatures()[i];
                modelData.getFeatures()[i] = (unnormalised - featureMinimums[i])
                                                / (featureMaximums[i] - featureMinimums[i]);
            }
        }
    }

    /**
     * @return [0] = minimums for each feature, [1] = maximums for each feature
     */
    public double[][] getFeatureMinsMaxes()
    {
        double[] minimums = new double[ModelData.N_FEATURES];
        Arrays.fill(minimums, Double.MAX_VALUE);
        double[] maximums = new double[ModelData.N_FEATURES];
        Arrays.fill(maximums, Double.MIN_VALUE);
        for (ModelData elem : data)
        {
            double[] quoteData = elem.getFeatures();
            for (int i = 0; i < ModelData.N_FEATURES; i ++)
            {
                if (minimums[i] > quoteData[i])
                    minimums[i] = quoteData[i];
                if (maximums[i] < quoteData[i])
                    maximums[i] = quoteData[i];
            }
        }
        return new double[][] {minimums, maximums};
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
        datasets.add(new ProcessedCorpus(topTerms, data.subList(0, index)));
        datasets.add(new ProcessedCorpus(topTerms, data.subList(index + 1, cardinality - 1)));
        return datasets;
    }

    public DataSet toDataSet(long seed)
    {
        int size = size();

        double[][] inputsMatrix = new double[size][ModelData.N_FEATURES];
        double[][] outputsMatrix = new double[size][ModelData.N_LABELS];

        int row = 0;
        for (ModelData data : this.data)
        {
            for (int col = 0; col < ModelData.N_FEATURES; col ++)
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

    public String[] getTopTerms()
    {
        return topTerms;
    }

}
