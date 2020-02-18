package net.benorourke.stocks.framework.series.data.impl;

import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.DataType;

import java.util.Date;

public class StockQuote extends Data
{
    private final double open, close;
    private final double high, low;
    private final long volume;

    public StockQuote(Date date, double open, double close, double high, double low, long volume)
    {
        super(DataType.STOCK_QUOTE, date);

        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.volume = volume;
    }

    public double getOpen()
    {
        return open;
    }

    public double getClose()
    {
        return close;
    }

    public double getHigh()
    {
        return high;
    }

    public double getLow()
    {
        return low;
    }

    public long getVolume()
    {
        return volume;
    }

}
