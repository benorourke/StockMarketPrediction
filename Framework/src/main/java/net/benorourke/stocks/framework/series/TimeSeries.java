package net.benorourke.stocks.framework.series;

import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.stock.Stock;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimeSeries
{
    private final UUID id;
    private String name;
    private Stock stock;
    /**
     * Stores the number of raw feedforward for DataSources that have been collected.
     */
    private Map<Class<? extends DataSource>, Integer> rawDataCounts;

    public TimeSeries(UUID id, String name, Stock stock, Map<Class<? extends DataSource>, Integer> rawDataCounts)
    {
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.rawDataCounts = rawDataCounts;
    }

    public TimeSeries(String name, Stock stock)
    {
        this(UUID.randomUUID(), name, stock, new HashMap<>());
    }

    @Override
    public String toString()
    {
        return '[' + name + ", " + stock.toString() + ']';
    }

    public UUID getId()
    {
        return id;
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
