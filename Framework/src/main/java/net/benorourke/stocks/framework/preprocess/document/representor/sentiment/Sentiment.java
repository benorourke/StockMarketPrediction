package net.benorourke.stocks.framework.preprocess.document.representor.sentiment;

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
