package net.benorourke.stocks.framework.collection;

import net.benorourke.stocks.framework.stock.Stock;
import net.benorourke.stocks.framework.util.DateUtil;

import java.util.Date;

public class Query
{
    private final Date to, from;
    private final Stock stock;

    public Query(Date to, Date from, Stock stock)
    {
        this.to = to;
        this.from = from;
        this.stock = stock;
    }

    @Override
    public String toString()
    {
        return "[" + stock.toString() + ", " + DateUtil.formatSimple(from)
                    + "->" + DateUtil.formatSimple(to) + ']';
    }

    public Date getTo()
    {
        return to;
    }

    public Date getFrom()
    {
        return from;
    }

    public Stock getStock()
    {
        return stock;
    }

}
