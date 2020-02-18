package net.benorourke.stocks.framework.series.data.impl;

import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.ProcessedData;

import java.util.Date;

/**
 * TODO
 */
public class ProcessedStockQuote extends ProcessedData
{
    private final double open, close;
    private final double high, low;
    // TODO - Add volume?

    // TODO - Normalise the data?

    public ProcessedStockQuote(Date date, double open, double close, double high, double low)
    {
        super(DataType.PROCESSED_STOCK_QUOTE, date);

        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
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
}
