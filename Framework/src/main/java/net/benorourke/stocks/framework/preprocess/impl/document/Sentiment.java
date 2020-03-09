package net.benorourke.stocks.framework.preprocess.impl.document;

public enum Sentiment
{
    VERY_POSITIVE(5, 1),
    POSITIVE(4, 0.75),
    NEUTRAL(3, 0.5),
    NEGATIVE(2, 0.25),
    VERY_NEGATIVE(1, 0);

    private final int id;
    private final double normalised;

    Sentiment(int id, double normalised)
    {
        this.id = id;
        this.normalised = normalised;
    }

    public static Sentiment fromStanfordClass(String strClass)
    {
        return valueOf(strClass.toUpperCase().replace(" ", "_"));
    }

    public int getId()
    {
        return id;
    }

    public double normalise()
    {
        return normalised;
    }

}
