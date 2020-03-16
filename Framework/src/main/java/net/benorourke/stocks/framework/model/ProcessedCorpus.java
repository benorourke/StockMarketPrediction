package net.benorourke.stocks.framework.model;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

import java.util.*;

public class ProcessedCorpus implements Iterable<ModelData>
{
    private final int numFeatures, numLabels;
    private final String[] topTerms;
    private final List<ModelData> data;
    // Have to be manually calculated; can't calculate on the fly if we normalise
    private double[] featureMinimums, featureMaximums;

    public ProcessedCorpus(int numFeatures, int numLabels, String[] topTerms, List<ModelData> data)
    {
        this.numFeatures = numFeatures;
        this.numLabels = numLabels;
        this.topTerms = topTerms;
        this.data = data;
    }

    public ProcessedCorpus(int numFeatures, int numLabels, String[] topTerms)
    {
        this(numFeatures, numLabels, topTerms, new ArrayList<>());
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
     */
    public void normalise(double[] featureMinimums, double[] featureMaximums)
    {
        for (ModelData modelData : data)
        {
            for (int i = 0; i < numFeatures; i ++)
            {
                double unnormalised = modelData.getFeatures()[i];

                if (featureMinimums[i] == featureMaximums[i]) // Prevent dividing by zero
                {
                    modelData.getFeatures()[i] = 0.0D;
                }
                else
                {
                    double normalised = (unnormalised - featureMinimums[i])
                            / (featureMaximums[i] - featureMinimums[i]);
                    modelData.getFeatures()[i] = normalised;
                }
            }
        }
    }

    public void normalise()
    {
        normalise(this.featureMinimums, this.featureMaximums);
    }

    /**
     * @return [0] = minimums for each feature, [1] = maximums for each feature
     */
    public void calculateFeatureMinsMaxes()
    {
        featureMinimums = new double[numFeatures];
        Arrays.fill(featureMinimums, Double.MAX_VALUE);
        featureMaximums = new double[numFeatures];
        Arrays.fill(featureMaximums, Double.MIN_VALUE);
        for (ModelData elem : data)
        {
            double[] quoteData = elem.getFeatures();
            for (int i = 0; i < numFeatures; i ++)
            {

                if (featureMinimums[i] > quoteData[i])
                    featureMinimums[i] = quoteData[i];
                if (featureMaximums[i] < quoteData[i])
                    featureMaximums[i] = quoteData[i];
            }
        }
    }

    /**
     *
     * @param splitRatio 0.6 would mean 60% in the first array, 40% in the second
     * @return
     */
    public List<ProcessedCorpus> split(double splitRatio)
    {
        int cardinality = size();
        int index;
        if (cardinality < 2)
            return null;
        if (cardinality == 2)
            index = 0;
        else
            index = (int) Math.round((cardinality - 1) * splitRatio);

        Framework.debug("[ProcessedCorpus] Splitting @" + index + " (cardinality=" + cardinality + ')');

        List<ProcessedCorpus> datasets = new ArrayList<>();
        datasets.add(new ProcessedCorpus(numFeatures, numLabels, topTerms, data.subList(0, index + 1)));
        datasets.add(new ProcessedCorpus(numFeatures, numLabels, topTerms, data.subList(index + 1, cardinality)));
        return datasets;
    }

    public DataSet toDataSet(long seed)
    {
        int size = size();

        double[][] inputsMatrix = new double[size][numFeatures];
        double[][] outputsMatrix = new double[size][numLabels];

        int row = 0;
        for (ModelData data : this.data)
        {
            for (int col = 0; col < numLabels; col ++)
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

    public int getNumFeatures()
    {
        return numFeatures;
    }

    public int getNumLabels()
    {
        return numLabels;
    }

    public String[] getTopTerms()
    {
        return topTerms;
    }

}
