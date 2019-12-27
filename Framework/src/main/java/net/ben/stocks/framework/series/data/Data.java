package net.ben.stocks.framework.series.data;

import java.util.Date;

public abstract class Data
{
    private final Date date;

    public Data(Date date)
    {
        this.date = date;
    }

    public abstract DataType getType();

    public abstract boolean isProcessed();

    public Date getDate()
    {
        return date;
    }

}
