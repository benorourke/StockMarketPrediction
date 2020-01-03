package net.ben.stocks.framework.series;

import net.ben.stocks.framework.stock.Stock;

public class TimeSeries
{
    private final String name;
    private final Stock stock;

    public TimeSeries(String name, Stock stock)
    {
        this.name = name;
        this.stock = stock;
    }

    public String getName()
    {
        return name;
    }

    public Stock getStock()
    {
        return stock;
    }
}
