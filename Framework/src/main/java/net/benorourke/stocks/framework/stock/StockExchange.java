package net.benorourke.stocks.framework.stock;

import java.util.*;

public class StockExchange
{
    private final String name;
    private final String shortName;
    private final List<Stock> stocks;

    public StockExchange(String name, String shortName)
    {
        this.name = name;
        this.shortName = shortName;
        this.stocks = new ArrayList<Stock>();
    }

    @Override
    public String toString()
    {
        return shortName;
    }

    @Override
    public boolean equals(Object obj)
    {
        return (obj instanceof StockExchange) ? obj.toString().equalsIgnoreCase(toString()) : false;
    }

    public String getName()
    {
        return name;
    }

    public String getShortName()
    {
        return shortName;
    }

    public List<Stock> getStocks()
    {
        return stocks;
    }

}
