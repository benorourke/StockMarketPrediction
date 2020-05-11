package net.benorourke.stocks.framework.model;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.preprocess.FeatureRepresentor;
import net.benorourke.stocks.framework.series.data.impl.CleanedDocument;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

import java.util.*;

/**
 * The dataset that contains all features and labels mapped against each other.
 */
public class ProcessedDataset implements Iterable<ModelData>
{
    /** The representors that were used to extract features from documents. */
    private final List<FeatureRepresentor<CleanedDocument>> documentFeatureRepresentors;
    /** The representors that were used to extract features from stock quotes. */
    private final List<FeatureRepresentor<StockQuote>> quoteFeatureRepresentors;
    /** The number of features & labels for each datapoint. */
    private final int numFeatures, numLabels;
    /** The data itself */
    private final List<ModelData> data;

    // Have to be manually calculated; can't calculate on the fly if we normalise
    private double[] featureMinimums, featureMaximums;

    /**
     * Create a new instance.
     *
     * @param documentFeatureRepresentors
     * @param quoteFeatureRepresentors
     * @param numFeatures
     * @param numLabels
     * @param data
     */
    public ProcessedDataset(List<FeatureRepresentor<CleanedDocument>> documentFeatureRepresentors,
                            List<FeatureRepresentor<StockQuote>> quoteFeatureRepresentors,
                            int numFeatures, int numLabels, List<ModelData> data)
    {
        this.documentFeatureRepresentors = documentFeatureRepresentors;
        this.quoteFeatureRepresentors = quoteFeatureRepresentors;
        this.numFeatures = numFeatures;
        this.numLabels = numLabels;
        this.data = data;
    }

    /**
     * Create a new instance with no datapoints.
     *
     * @param documentFeatureRepresentors
     * @param quoteFeatureRepresentors
     * @param numFeatures
     * @param numLabels
     */
    public ProcessedDataset(List<FeatureRepresentor<CleanedDocument>> documentFeatureRepresentors,
                            List<FeatureRepresentor<StockQuote>> quoteFeatureRepresentors,
                            int numFeatures, int numLabels)
    {
        this(documentFeatureRepresentors, quoteFeatureRepresentors, numFeatures, numLabels, new ArrayList<>());
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
     * Normalise the features by specifying the minimums & maximums.
     *
     * @param featureMinimums the minimum values for each feature
     * @param featureMaximums the maximum values for each feature
     */
    public void normalise(double[] featureMinimums, double[] featureMaximums)
    {
        for (ModelData modelData : data)
        {
            for (int i = 0; i < numFeatures; i ++)
            {
                double unnormalised = modelData.getFeatures()[i];

                if (featureMinimums[i] == featureMaximums[i]) // Prevent dividing by zero
                    modelData.getFeatures()[i] = 0.0D;
                else
                {
                    // Safely normalise the features
                    double normalised = (unnormalised - featureMinimums[i])
                                            / (featureMaximums[i] - featureMinimums[i]);
                    modelData.getFeatures()[i] = normalised;
                }
            }
        }
    }

    /**
     * Normalise the features using the cached minimum & maximum features.
     *
     * Note: {@link #calculateFeatureMinsMaxes()} must be called at least once before calling this.
      */
    public void normalise()
    {
        normalise(this.featureMinimums, this.featureMaximums);
    }

    /**
     * Calculate and cache internally the minimum & maximum values for each feature.
     */
    public void calculateFeatureMinsMaxes()
    {
        featureMinimums = featureMaximums = null;
        for (ModelData elem : data)
        {
            // Set the mins & maxes to the first element
            if (featureMinimums == null)
            {
                featureMinimums = Arrays.copyOf(elem.getFeatures(), numFeatures);
                featureMaximums = Arrays.copyOf(elem.getFeatures(), numFeatures);
                continue;
            }

            // Deduce whether the features of this datapoint are either maximums / minimums
            double[] features = elem.getFeatures();
            for (int i = 0; i < numFeatures; i ++)
            {
                if (featureMinimums[i] > features[i])
                    featureMinimums[i] = features[i];
                if (featureMaximums[i] < features[i])
                    featureMaximums[i] = features[i];
            }
        }

        Framework.info("Calculated Feature Mins/Maxes");
    }

    /**
     * Shuffle the elements within the dataset to ensure stochastic behaviour.
     *
     * @param seed the seed by which to randomise
     */
    public void shuffle(long seed)
    {
        Collections.shuffle(data, new Random(seed));
    }

    /**
     * Split a dataset based on a ratio. A list containing two new instances of split datasets.
     *
     * If the cardinality is < 2, null will be returned.
     *
     * @param splitRatio 0.6 would mean 60% in the first list, 40% in the second
     * @return the split datasets
     */
    public List<ProcessedDataset> split(double splitRatio)
    {
        int cardinality = size();
        int index;
        if (cardinality < 2)
            return null;
        if (cardinality == 2)
            index = 0;
        else
            index = (int) Math.floor((cardinality - 1) * splitRatio);

        Framework.info("[ProcessedDataset] Splitting @" + index + " (cardinality=" + cardinality + ')');

        List<ProcessedDataset> datasets = new ArrayList<>();
        datasets.add(new ProcessedDataset(documentFeatureRepresentors, quoteFeatureRepresentors,
                                          numFeatures, numLabels, data.subList(0, index + 1)));
        datasets.add(new ProcessedDataset(documentFeatureRepresentors, quoteFeatureRepresentors,
                                          numFeatures, numLabels, data.subList(index + 1, cardinality)));
        return datasets;
    }

    /**
     * Convert this object to a DataSet that can be used to train models in DeepLearning4J.
     *
     * @param seed the seed by which to shuffle
     * @return the resulting dataset
     */
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

    public List<FeatureRepresentor<CleanedDocument>> getDocumentFeatureRepresentors()
    {
        return documentFeatureRepresentors;
    }

    public List<FeatureRepresentor<StockQuote>> getQuoteFeatureRepresentors()
    {
        return quoteFeatureRepresentors;
    }

}
