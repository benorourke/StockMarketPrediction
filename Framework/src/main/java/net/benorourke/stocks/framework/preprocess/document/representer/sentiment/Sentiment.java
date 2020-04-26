package net.benorourke.stocks.framework.preprocess.document.representer.sentiment;

import java.util.Arrays;

public enum Sentiment
{
    VERY_NEGATIVE,
    NEGATIVE,
    NEUTRAL,
    VERY_POSITIVE,
    POSITIVE;

    public static Sentiment fromStanfordClass(String strClass)
    {
        return valueOf(strClass.toUpperCase().replace(" ", "_"));
    }

}
