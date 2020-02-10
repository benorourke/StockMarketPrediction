package net.ben.stocks.framework.series.data;

import java.util.Date;

/**
 * TODO
 */
public class ProcessedStockQuote extends Data
{
    private final double open, close;
    private final double high, low;
    // TODO - Add volume?

    public ProcessedStockQuote(Date date, double open, double close, double high, double low)
    {
        super(DataType.PROCESSED_STOCK_QUOTE, date);

        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
    }

    @Override
    public boolean isProcessed()
    {
        // TODO - Normalisation? Research whether this is better
        return true;
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