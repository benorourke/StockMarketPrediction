package net.benorourke.stocks.framework.preprocess.assignment;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.model.ModelData;
import net.benorourke.stocks.framework.preprocess.FeatureRepresenter;
import net.benorourke.stocks.framework.series.data.impl.*;
import net.benorourke.stocks.framework.util.DateUtil;
import net.benorourke.stocks.framework.util.StringUtil;
import net.benorourke.stocks.framework.util.Tuple;

import java.util.*;
import java.util.stream.Collectors;

public class AverageDataMapper extends ModelDataMapper
{

    /**
     * @param documentRepresenters
     * @param quoteRepresenters    the feature representers for extracting vectors from the stock quotes
     * @param labelsToPredict
     */
    public AverageDataMapper(List<FeatureRepresenter<CleanedDocument>> documentRepresenters,
                             List<FeatureRepresenter<StockQuote>> quoteRepresenters,
                             StockQuoteDataType[] labelsToPredict)
    {
        super(documentRepresenters, quoteRepresenters, labelsToPredict);
    }

    @Override
    public ModelData toModelData(Date date, List<ProcessedDocument> documents, List<StockQuote> quotes)
    {
        // FEATURES FROM DOCUMENTS
        double[] documentFeatures = new double[0];
        for (FeatureRepresenter<CleanedDocument> representer : getDocumentRepresenters())
        {
            double[][] allFeatures = new double[documents.size()][];
            int idx = 0;
            for (ProcessedDocument document : documents)
                allFeatures[idx ++] = document.getFeatureVectors().get(representer);

            double[] average = handleAverage(representer.getCombinationPolicy(), allFeatures);
            documentFeatures = combineFeatures(documentFeatures, average);

            Framework.debug("Combined " + representer.getName());
        }

        // FEATURES FROM QUOTES
        for (FeatureRepresenter<StockQuote> quoteRepresenter : getQuoteRepresenters())
            quoteRepresenter.initialise(quotes);
        double[] featuresFromQuotes = featuresFromQuotes(quotes);

        // Ordering doesn't make a difference
        double[] features = combineFeatures(featuresFromQuotes, documentFeatures);

        // LABELS
        double[] labels = labelsFromQuotes(quotes);

        assert features.length == getFeatureCount();
        return new ModelData(date, features, labels);
    }

    private double[] combineFeatures(double[]... allFeatures)
    {
        int size = 0;
        for (double[] features : allFeatures)
            size += features.length;

        double[] combined = new double[size];
        int nextIdx = 0;
        for (double[] features : allFeatures)
            for (double feature : features)
                combined[nextIdx ++] = feature;

        return combined;
    }

    private double[] featuresFromQuotes(List<StockQuote> quotes)
    {
        double[] features = new double[0];
        for (FeatureRepresenter<StockQuote> representer : getQuoteRepresenters())
            features = combineFeatures(features, featureFromQuotes(representer, quotes));

        return features;
    }

    private double[] featureFromQuotes(FeatureRepresenter<StockQuote> representer, List<StockQuote> quotes)
    {
        double[][] features = new double[quotes.size()][];

        int idx = 0;
        for (StockQuote quote : quotes)
            features[idx ++] = representer.getVectorRepresentation(quote);

        return handleAverage(representer.getCombinationPolicy(), features);
    }

    private double[] labelsFromQuotes(List<StockQuote> quotes)
    {
        // Take average of all quotes
        int quoteCols = StockQuoteDataType.values().length;
        double[][] quoteFeatures = new double[quotes.size()][quoteCols];
        int quoteFeaturesIdx = 0;
        for (StockQuote quote : quotes)
            quoteFeatures[quoteFeaturesIdx ++] = quote.getData();
        double[] quoteFeaturesVector = takeMean(quoteFeatures, quoteCols);

        double[] labels = new double[getLabelCount()];
        for (int i = 0; i < getLabelsToPredict().length; i ++)
        {
            StockQuoteDataType type = getLabelsToPredict()[i];
            labels[i] = quoteFeaturesVector[type.index()];
        }
        return labels;
    }

    private double[] handleAverage(FeatureRepresenter.CombinationPolicy policy, double[][] toCombine)
    {
        int featureCount = toCombine[0].length;
        switch (policy)
        {
            case TAKE_HIGHEST:

                Framework.debug("Taking highest of:");
                for (double[] features : toCombine)
                    Framework.debug("   " + StringUtil.formatDoubles(features));
                Framework.debug("Result: " + StringUtil.formatDoubles(takeHighest(toCombine, featureCount)));

                return takeHighest(toCombine, featureCount);

            case TAKE_MODE_AVERAGE:
                return takeMode(toCombine, featureCount);

            default:
            case TAKE_MEAN_AVERAGE:
                return takeMean(toCombine, featureCount);
        }
    }

    public double[] takeHighest(double[][] arrays, int featureCount)
    {
        double[] result = new double[featureCount];
        for (double[] features : arrays)
        {
            int idx = 0;
            for (double feature : features)
            {
                if (result[idx] < feature)
                    result[idx] = feature;

                idx ++;
            }
        }
        return result;
    }

    public double[] takeMean(double[][] arrays, int featureCount)
    {
        double[] totals = new double[featureCount];
        for (int j = 0; j < arrays.length; j ++)
        {
            double[] vector = arrays[j];
            for (int i = 0; i < featureCount; i ++)
                totals[i] = totals[i] + vector[i];
        }

        double[] averages = new double[featureCount];
        double rows = (double) arrays.length;
        for (int i = 0; i < featureCount; i ++)
            averages[i] = totals[i] / rows;

        return averages;
    }

    /**
     * If there are several features with the same frequency, a seemingly random frequency will be returned (based on
     * the hash of the feature itself; which is normally distributed)
     * @param arrays
     * @param featureCount
     * @return
     */
    public double[] takeMode(double[][] arrays, int featureCount)
    {
        double[] result = new double[featureCount];

        // Map:
        //    Key (Tuple):
        //       Key = feature index
        //       Value = feature
        //    Value: Frequency of feature for given index
        Map<Tuple<Integer, Double>, Integer> frequencies = new HashMap<>();
        for (double[] features : arrays)
        {
            int idx = 0;
            for (double feature : features)
            {
                Tuple<Integer, Double> tuple = new Tuple<>(idx, feature);

                if (!frequencies.containsKey(tuple))
                    frequencies.put(tuple, 0);

                frequencies.put(tuple, frequencies.get(tuple) + 1);

                idx ++;
            }
        }

        for (int featureIndex = 0; featureIndex < result.length; featureIndex ++)
        {
            final int finalFeatureIndex = featureIndex;
            List<Map.Entry<Tuple<Integer, Double>, Integer>> entries =
                    frequencies.entrySet()
                            .stream()
                            .filter(e -> e.getKey().getA() == finalFeatureIndex)
                            .collect(Collectors.toList());

            int frequency = 0;
            for (Map.Entry<Tuple<Integer, Double>, Integer> entry : entries)
            {
                if (entry.getValue() > frequency)
                {
                    result[featureIndex] = entry.getKey().getB();
                    frequency = entry.getValue();
                }
            }
        }

        return result;
    }

}
