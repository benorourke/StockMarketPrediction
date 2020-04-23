package net.benorourke.stocks.framework.collection;

import net.benorourke.stocks.framework.util.DateUtil;

import java.util.Date;

public class Query
{
    private final Date to, from;

    public Query(Date to, Date from)
    {
        this.to = to;
        this.from = from;
    }

    @Override
    public String toString()
    {
        return "[".concat(DateUtil.formatSimple(from)).concat("->").concat(DateUtil.formatSimple(to)).concat("]");
    }

    public Date getTo()
    {
        return to;
    }

    public Date getFrom()
    {
        return from;
    }

}
