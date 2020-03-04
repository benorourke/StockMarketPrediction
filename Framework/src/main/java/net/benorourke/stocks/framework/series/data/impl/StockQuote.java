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

        data = new double[StockQuoteDataType.count()];

        data[StockQuoteDataType.OPEN.index()] = open;
        data[StockQuoteDataType.CLOSE.index()] = close;
        data[StockQuoteDataType.HIGH.index()] = high;
        data[StockQuoteDataType.LOW.index()] = low;
        data[StockQuoteDataType.VOLUME.index()] = (double) volume;
    }

    public double getOpen()
    {
        return data[StockQuoteDataType.OPEN.index()];
    }

    public double[] getData()
    {
        return data;
    }

    public double getClose()
    {
        return data[StockQuoteDataType.CLOSE.index()];
    }

    public double getHigh()
    {
        return data[StockQuoteDataType.HIGH.index()];
    }

    public double getLow()
    {
        return data[StockQuoteDataType.LOW.index()];
    }

    public double getVolume()
    {
        return data[StockQuoteDataType.VOLUME.index()];
    }

}
