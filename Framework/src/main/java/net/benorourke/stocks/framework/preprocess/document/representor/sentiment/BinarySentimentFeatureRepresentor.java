package net.benorourke.stocks.framework.preprocess.document.representor.sentiment;

import net.benorourke.stocks.framework.series.data.impl.CleanedDocument;

import java.util.Arrays;

/**
 * For a Sentiment of {@link Sentiment#VERY_POSITIVE}, the resultant vector would be [1, 0, 0, 0, 0].
 */
public class BinarySentimentFeatureRepresentor extends SentimentFeatureRepresentor
{

    @Override
    public int getVectorSize()
    {
        return Sentiment.values().length;
    }

    @Override
    public double[] getVectorRepresentation(CleanedDocument datapoint)
    {
        Sentiment sentiment = determineSentiment(datapoint);
        double[] vector = new double[Sentiment.values().length];
        Arrays.fill(vector, 0);
        vector[sentiment.ordinal()] = 1;
        return vector;
    }

}
