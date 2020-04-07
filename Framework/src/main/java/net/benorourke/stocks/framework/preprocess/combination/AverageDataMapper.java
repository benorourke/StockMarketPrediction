package net.benorourke.stocks.framework.preprocess.combination;

import edu.stanford.nlp.maxent.Feature;
import net.benorourke.stocks.framework.model.ModelData;
import net.benorourke.stocks.framework.preprocess.FeatureRepresenter;
import net.benorourke.stocks.framework.series.data.impl.Document;
import net.benorourke.stocks.framework.series.data.impl.ProcessedDocument;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;
import net.benorourke.stocks.framework.series.data.impl.StockQuoteDataType;
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
    public AverageDataMapper(List<FeatureRepresenter<Document>> documentRepresenters,
                             List<FeatureRepresenter<StockQuote>> quoteRepresenters,
                             StockQuoteDataType[] labelsToPredict)
    {
        super(documentRepresenters, quoteRepresenters, labelsToPredict);
    }

    @Override
    public ModelData toModelData(Date date, List<ProcessedDocument> documents, List<StockQuote> quotes)
    {
        // TODO: Document features (stored within processeddocument)

        // LABELS
        double[] labels = labelsFromQuotes(quotes);


        return new ModelData(date, featuresFromQuotes(quotes), labels);
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

    /**
     *
     * arrays[i].length should equal cols
     *
     * @param arrays
     * @param cols
     * @return
     */
    public double[] takeMean(double[][] arrays, int cols)
    {
        double[] totals = new double[cols];
        for (int j = 0; j < arrays.length; j ++)
        {
            double[] vals = arrays[j];
            for (int i = 0; i < totals.length; i ++)
            {
                totals[i] = totals[i] + vals[i];
            }
        }

        double[] averages = new double[cols];
        double rows = (double) arrays.length;
        for (int i = 0; i < totals.length; i ++)
        {
            averages[i] = totals[i] / rows;
        }
        return averages;
    }

    // TODO - TEST
    private double[] handleAverage(FeatureRepresenter.CombinationPolicy policy, double[][] toCombine)
    {
        double[] result = new double[toCombine[0].length];
        switch (policy)
        {
            case TAKE_HIGHEST:

                for (double[] features : toCombine)
                {
                    int idx = 0;
                    for (double feature : features)
                    {
                        if (result[idx] < feature)
                            result[idx] = feature;

                        idx ++;
                    }
                }
                break;

            case TAKE_MEAN_AVERAGE:
                result = takeMean(toCombine, toCombine[0].length);
                break;

            case TAKE_MODE_AVERAGE:

                // Map:
                //    Key (Tuple):
                //       Key = feature index
                //       Value = feature
                //    Value: Frequency of feature for given index
                Map<Tuple<Integer, Double>, Integer> frequencies = new HashMap<>();
                for (double[] features : toCombine)
                {
                    int idx = 0;
                    for (double feature : features)
                    {
                        Tuple<Integer, Double> tuple = new Tuple<>(idx, feature);

                        if (!frequencies.containsKey(tuple))
                            frequencies.put(tuple, 0);

                        frequencies.put(tuple, frequencies.get(tuple));

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
                break;
        }
        return result;
    }

}
