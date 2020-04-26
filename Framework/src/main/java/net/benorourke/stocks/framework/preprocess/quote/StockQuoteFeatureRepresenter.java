package net.benorourke.stocks.framework.preprocess.quote;

import net.benorourke.stocks.framework.preprocess.FeatureRepresenter;
import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;
import net.benorourke.stocks.framework.series.data.impl.StockQuoteDataType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple representer that will use every {@link#StockQuoteDataType}
 */
public class StockQuoteFeatureRepresenter implements FeatureRepresenter<StockQuote>
{
    private StockQuoteDataType[] dataTypes;

    public StockQuoteFeatureRepresenter(StockQuoteDataType[] dataTypes)
    {
        this.dataTypes = dataTypes;
    }

    @Override
    public DataType<StockQuote> getTypeFor()
    {
        return DataType.STOCK_QUOTE;
    }

    @Override
    public void initialise(List<StockQuote> allData) {}

    @Override
    public int getVectorSize()
    {
        return dataTypes.length;
    }

    @Override
    public double[] getVectorRepresentation(StockQuote datapoint)
    {
        double[] vector = new double[dataTypes.length];

        for (int i = 0; i < dataTypes.length; i ++)
        {
            StockQuoteDataType type = dataTypes[i];
            vector[i] = datapoint.getData()[type.ordinal()];
        }

        return vector;
    }

    @Override
    public String getName()
    {
        return "Stock Quote";
    }

    @Override
    public CombinationPolicy getCombinationPolicy()
    {
        return CombinationPolicy.TAKE_MEAN_AVERAGE;
    }

    public StockQuoteDataType[] getDataTypes()
    {
        return dataTypes;
    }

}
