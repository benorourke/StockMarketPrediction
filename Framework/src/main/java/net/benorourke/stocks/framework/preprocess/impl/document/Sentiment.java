package net.benorourke.stocks.framework.preprocess.impl.document;

public enum Sentiment
{
    VERY_POSITIVE(5),
    POSITIVE(4),
    NEUTRAL(3),
    NEGATIVE(2),
    VERY_NEGATIVE(1);

    private final int id;

    Sentiment(int id)
    {
        this.id = id;
    }

    public static Sentiment fromStanfordClass(String strClass)
    {
        return valueOf(strClass.toUpperCase().replace(" ", "_"));
    }

    public int getId()
    {
        return id;
    }
}
