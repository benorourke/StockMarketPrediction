package net.benorourke.stocks.framework.preprocess.document;

import java.util.Arrays;

public enum Sentiment
{
    VERY_POSITIVE(4),
    POSITIVE(3),
    NEUTRAL(2),
    NEGATIVE(1),
    VERY_NEGATIVE(0);

    private final int index;

    Sentiment(int index)
    {
        this.index = index;
    }

    public static Sentiment fromStanfordClass(String strClass)
    {
        return valueOf(strClass.toUpperCase().replace(" ", "_"));
    }

    public double[] toInputVector()
    {
        double[] vector = new double[values().length];
        Arrays.fill(vector, 0);
        vector[index] = 1;
        return vector;
    }

}
