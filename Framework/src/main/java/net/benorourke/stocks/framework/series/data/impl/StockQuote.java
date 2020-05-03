package net.benorourke.stocks.framework.series.data.impl;

import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.RawDataAnnotation;
import net.benorourke.stocks.framework.series.data.RawDataElementAnnotation;

import java.util.Date;

public class StockQuote extends Data
{
    @RawDataElementAnnotation
    private double open;
    @RawDataElementAnnotation
    private double close;
    @RawDataElementAnnotation
    private double high;
    @RawDataElementAnnotation
    private double low;
    @RawDataElementAnnotation
    private double volume;

    @RawDataAnnotation(indexOfDate = 0, paramOrder = {"open", "close", "high", "low", "volume"})
    public StockQuote(Date date, double open, double close, double high, double low, double volume)
    {
        super(DataType.STOCK_QUOTE, date);

        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.volume = volume;
    }

    @Override
    public boolean isDuplicate(Data other)
    {
        if (!(other instanceof StockQuote))
            return false;

        double[] thisData = toVector();
        double[] otherData = ((StockQuote) other).toVector();
        for (int i = 0; i < thisData.length; i ++)
            if (thisData[i] != otherData[i])
                return false;

        return true;
    }

    /**
     * Do not use this to mutate the data within this class.
     *
     * @return
     */
    public double[] toVector()
    {
        double[] data = new double[StockQuoteDataType.values().length];
        data[StockQuoteDataType.OPEN.index()] = open;
        data[StockQuoteDataType.CLOSE.index()] = close;
        data[StockQuoteDataType.HIGH.index()] = high;
        data[StockQuoteDataType.LOW.index()] = low;
        data[StockQuoteDataType.VOLUME.index()] = volume;
        return data;
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

    public double getVolume()
    {
        return volume;
    }

}
