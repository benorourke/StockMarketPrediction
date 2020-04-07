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
        return StockQuoteDataType.values().length;
    }

    @Override
    public double[] getVectorRepresentation(StockQuote datapoint)
    {
        return Arrays.copyOf(datapoint.getData(), getVectorSize());
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

}
