package net.ben.stocks.framework.series.data;

import java.util.Date;

public abstract class Data
{
    private final DataType type;
    private final Date date;

    public Data(DataType type, Date date)
    {
        this.type = type;
        this.date = date;
    }

    public abstract boolean isProcessed();

    public DataType getType()
    {
        return type;
    }

    public Date getDate()
    {
        return date;
    }

}
