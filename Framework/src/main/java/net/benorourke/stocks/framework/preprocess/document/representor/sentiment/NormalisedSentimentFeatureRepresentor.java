package net.benorourke.stocks.framework.preprocess.document.representor.sentiment;

import net.benorourke.stocks.framework.series.data.impl.CleanedDocument;

/**
 * Example steps for sentiments:
 * 1) {@link Sentiment#VERY_POSITIVE}: 1.0
 * 2) {@link Sentiment#POSITIVE}: 0.75
 * 3) {@link Sentiment#NEUTRAL}: 0.5
 * 4) {@link Sentiment#NEGATIVE}: 0.25
 * 5) {@link Sentiment#VERY_NEGATIVE}: 0.0
 */
public class NormalisedSentimentFeatureRepresentor extends SentimentFeatureRepresentor
{
    private static final double STEP = 1.0D / ((double) Sentiment.values().length - 1.0);

    @Override
    public int getVectorSize()
    {
        return 1;
    }

    @Override
    public double[] getVectorRepresentation(CleanedDocument datapoint)
    {
        Sentiment sentiment = determineSentiment(datapoint);
        return new double[] { STEP * sentiment.ordinal() };
    }

}
