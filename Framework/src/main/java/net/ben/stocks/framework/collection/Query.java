package net.ben.stocks.framework.collection;

import net.ben.stocks.framework.stock.Stock;
import net.ben.stocks.framework.util.DateUtil;

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
        return "[" + stock.toString() + ", " + DateUtil.formatSimpleUK(from)
                    + "->" + DateUtil.formatSimpleUK(to) + ']';
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
