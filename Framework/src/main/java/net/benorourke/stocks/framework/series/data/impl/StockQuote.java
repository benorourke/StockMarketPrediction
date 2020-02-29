package net.benorourke.stocks.framework.series.data.impl;

import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.DataType;

import java.util.Date;

public class StockQuote extends Data
{
    private final double[] data;

    public StockQuote(Date date, double open, double close, double high, double low, long volume)
    {
        super(DataType.STOCK_QUOTE, date);

        data = new double[StockQuoteCategory.count()];

        data[StockQuoteCategory.OPEN.index()] = open;
        data[StockQuoteCategory.CLOSE.index()] = close;
        data[StockQuoteCategory.HIGH.index()] = high;
        data[StockQuoteCategory.LOW.index()] = low;
        data[StockQuoteCategory.VOLUME.index()] = (double) volume;
    }

    public double getOpen()
    {
        return data[StockQuoteCategory.OPEN.index()];
    }

    public double[] getData()
    {
        return data;
    }

    public double getClose()
    {
        return data[StockQuoteCategory.CLOSE.index()];
    }

    public double getHigh()
    {
        return data[StockQuoteCategory.HIGH.index()];
    }

    public double getLow()
    {
        return data[StockQuoteCategory.LOW.index()];
    }

    public double getVolume()
    {
        return data[StockQuoteCategory.VOLUME.index()];
    }

}
