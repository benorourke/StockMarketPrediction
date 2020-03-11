package net.benorourke.stocks.framework.preprocess.combination;

import net.benorourke.stocks.framework.model.ModelData;
import net.benorourke.stocks.framework.preprocess.document.Sentiment;
import net.benorourke.stocks.framework.series.data.impl.ProcessedDocument;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;
import net.benorourke.stocks.framework.series.data.impl.StockQuoteDataType;

import java.util.*;

public class AverageDataMapper implements ModelDataMapper
{
    private static final int QUOTE_INPUTS     = StockQuoteDataType.values().length;
    private static final int SENTIMENT_INPUTS = Sentiment.values().length;

    @Override
    public int getFeatureCount(List<ProcessedDocument> documents, List<StockQuote> quotes)
    {
        // [0 .. 4] -> StockQuote Data (Real)
        // [5 .. 9] -> Sentiment Data (Boolean; only 1 true)
        // [10 .. 10 + number of top terms] -> Top Term Data (Boolean)
        return QUOTE_INPUTS + SENTIMENT_INPUTS + documents.get(0).getTopTermVector().length;
    }

    @Override
    public int getLabelCount(List<ProcessedDocument> documents, List<StockQuote> quotes)
    {
        return 1;
    }

    @Override
    public ModelData toModelData(Date date, List<ProcessedDocument> documents, List<StockQuote> quotes)
    {
        // FEATURES
        int numFeatures = getFeatureCount(documents, quotes);

        // quotes
        int quoteFeaturesCols = quotes.get(0).getData().length;
        double[][] quoteFeatures = new double[quotes.size()][quoteFeaturesCols];
        double[] quoteFeaturesVector = takeAverage(quoteFeatures, quoteFeaturesCols);
        // sentiment
        double[] sentimentVector = getModeSentiment(documents).toInputVector();
        // top terms
        double[] topTermsVector = orTopTerms(documents);

        double[] features = new double[numFeatures];
        for (int i = 0; i < QUOTE_INPUTS; i ++)
            features[i] = quoteFeaturesVector[i];
        for (int i = 0; i < SENTIMENT_INPUTS; i ++)
            features[QUOTE_INPUTS + i] = sentimentVector[i];
        for (int i = 0; i < topTermsVector.length; i ++)
            features[QUOTE_INPUTS + SENTIMENT_INPUTS + i] = topTermsVector[i];

        // LABELS
        double[] labels = new double[] {quoteFeaturesVector[StockQuoteDataType.CLOSE.index()]};

        return new ModelData(date, features, labels);
    }

    /**
     *
     * arrays[i].length should equal cols
     *
     * @param arrays
     * @param cols
     * @return
     */
    public double[] takeAverage(double[][] arrays, int cols)
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

    public Sentiment getModeSentiment(List<ProcessedDocument> documents)
    {
        Map<Sentiment, Integer> sentimentCounts = new HashMap<>();
        for (ProcessedDocument document : documents)
        {
            Sentiment sentiment = document.getSentiment();
            if (sentimentCounts.containsKey(sentiment))
                sentimentCounts.put(sentiment, sentimentCounts.get(sentiment) + 1);
            else
                sentimentCounts.put(sentiment, 1);
        }

        // Determine the mode Sentiment
        Sentiment mode = Sentiment.NEUTRAL;
        int cardinality = 0;
        for (Map.Entry<Sentiment, Integer> entry : sentimentCounts.entrySet())
        {
            if (entry.getValue() > cardinality)
            {
                mode = entry.getKey();
                cardinality = entry.getValue();
            }
        }

        return mode;
    }

    public double[] orTopTerms(List<ProcessedDocument> documents)
    {
        double[] result = new double[documents.get(0).getTopTermVector().length];

        for (ProcessedDocument document : documents)
        {
            boolean[] vector = document.getTopTermVector();
            for (int i = 0; i < vector.length; i ++)
            {
                if (vector[i])
                    result[i] = 1.0D;
            }
        }

        return result;
    }

//    public double[] getModeTopTerms(List<ProcessedDocument> documents)
//    {
//        int[] zeroCounts = new int[documents.get(0).getTopTermVector().length];
//        int[] onesCounts = new int[zeroCounts.length];
//
//        for (ProcessedDocument document : documents)
//        {
//            boolean[] vector = document.getTopTermVector();
//            for (int i = 0; i < vector.length; i ++)
//            {
//                if (vector[i])
//                    onesCounts[i] ++;
//                else
//                    zeroCounts[i] ++;
//            }
//        }
//
//        // Take the averages
//        double[] result = new double[zeroCounts.length];
//        for (int i = 0; i < onesCounts.length; i ++)
//        {
//            if (zeroCounts[i] == onesCounts[i])
//                result[i] = 1.0; // Should this be the case?
//            else if (zeroCounts[i] > onesCounts[i])
//                result[i] = 0.0;
//            else if (zeroCounts[i] < onesCounts[i])
//                result[i] = 1.0;
//        }
//        return result;
//    }

}
