package net.benorourke.stocks.framework.series.data.impl;

public enum StockQuoteDataType
{
    OPEN("Open", 0),
    CLOSE("Close", 1),
    HIGH("High", 2),
    LOW("Low", 3),
    VOLUME("Volume", 4);

    private final String locale;
    private final int index;

    StockQuoteDataType(String locale, int index)
    {
        this.locale = locale;
        this.index = index;
    }

    public String getLocale()
    {
        return locale;
    }

    public int index()
    {
        return index;
    }

    public static int count()
    {
        return values().length;
    }

}
