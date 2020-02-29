package net.benorourke.stocks.framework.series.data.impl;

import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.ProcessedData;

import java.util.Date;

public class NormalisedStockQuote extends ProcessedData
{
    private final double[] data;

    public NormalisedStockQuote(Date date, double[] data)
    {
        super(DataType.NORMALISED_STOCK_QUOTE, date);

        this.data = data;
    }

    public NormalisedStockQuote(Date date, double open, double close, double high, double low, long volume)
    {
        super(DataType.NORMALISED_STOCK_QUOTE, date);

        data = new double[StockQuoteCategory.count()];

        data[StockQuoteCategory.OPEN.index()] = open;
        data[StockQuoteCategory.CLOSE.index()] = close;
        data[StockQuoteCategory.HIGH.index()] = high;
        data[StockQuoteCategory.LOW.index()] = low;
        data[StockQuoteCategory.VOLUME.index()] = (double) volume;
    }

    public double[] getData()
    {
        return data;
    }

    public double getOpen()
    {
        return data[StockQuoteCategory.OPEN.index()];
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
