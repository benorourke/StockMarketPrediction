package net.benorourke.stocks.framework.series.data.impl;

import net.benorourke.stocks.framework.series.data.*;

import java.util.Date;
import java.util.UUID;

/**
 * A technical indicator for a given stock.
 */
public class StockQuote extends Data implements IdentifiableData
{
    /**
     * A unique ID to distinguish raw quotes from one another efficiently.
     *
     * Used to remove raw data.
     */
    private final UUID id;

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

    /**
     * Create a new instance
     *
     * @param id the unique identifier for this document.
     * @param date the date this document was created
     * @param date
     * @param open
     * @param close
     * @param high
     * @param low
     * @param volume
     */
    public StockQuote(UUID id, Date date, double open, double close, double high, double low, double volume)
    {
        super (DataType.STOCK_QUOTE, date);

        this.id = id;
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.volume = volume;
    }

    /**
     * The constructor used when injecting StockQuotes dynamically through the UI.
     *
     * @param date the date this document was created
     * @param open
     * @param close
     * @param high
     * @param low
     * @param volume
     */
    @RawDataConstructorAnnotation(indexOfDate = 0, paramOrder = {"open", "close", "high", "low", "volume"})
    public StockQuote(Date date, double open, double close, double high, double low, double volume)
    {
        this (UUID.randomUUID(), date, open, close, high, low, volume);
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

    @Override
    public UUID getId()
    {
        return id;
    }

    /**
     * Get the indicator as a vector, rather than individual constituents.
     *
     * Do not use this to mutate the data within this class.
     *
     * @return the data vector
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
