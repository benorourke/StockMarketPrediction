package net.benorourke.stocks.framework.series.data.impl;

import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.ProcessedData;

import java.util.Date;

public class NormalisedStockQuote extends ProcessedData
{
    private final double[] unnormalised;
    private final double[] normalised;

    public NormalisedStockQuote(Date date, double[] unnormalised, double[] normalised)
    {
        super(DataType.NORMALISED_STOCK_QUOTE, date);

        this.normalised = normalised;
        this.unnormalised = unnormalised;
    }

    public double[] getUnnormalisedData()
    {
        return unnormalised;
    }

    public double[] getNormalisedData()
    {
        return normalised;
    }

}
