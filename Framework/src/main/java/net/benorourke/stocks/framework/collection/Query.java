package net.benorourke.stocks.framework.collection;

import net.benorourke.stocks.framework.util.DateUtil;

import java.util.Date;

/**
 * A simple struct providing a date range.
 */
public class Query
{
    private final Date to, from;

    /**
     * Create a new instance
     *
     * @param to end of the range
     * @param from start of the range
     */
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
