package net.benorourke.stocks.framework.series;

import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.stock.Stock;

import java.util.HashMap;
import java.util.Map;

public class TimeSeries
{
    private String name;
    private Stock stock;
    /**
     * Stores the number of raw data for DataSources that have been collected.
     */
    private Map<Class<? extends DataSource>, Integer> rawDataCounts;

    public TimeSeries(String name, Stock stock, Map<Class<? extends DataSource>, Integer> rawDataCounts)
    {
        this.name = name;
        this.stock = stock;
        this.rawDataCounts = rawDataCounts;
    }

    public TimeSeries(String name, Stock stock)
    {
        this(name, stock, new HashMap<>());
    }

    @Override
    public String toString()
    {
        return '[' + name + ", " + stock.toString() + ']';
    }

    public String getName()
    {
        return name;
    }

    public Stock getStock()
    {
        return stock;
    }

    public Map<Class<? extends DataSource>, Integer> getRawDataCounts()
    {
        return rawDataCounts;
    }

}
