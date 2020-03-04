package net.benorourke.stocks.framework.series.data.impl;

public enum StockQuoteDataType
{
    OPEN(0),
    CLOSE(1),
    HIGH(2),
    LOW(3),
    VOLUME(4);

    private final int index;

    StockQuoteDataType(int index)
    {
        this.index = index;
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
