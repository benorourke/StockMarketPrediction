package net.benorourke.stocks.framework.series.data;

import java.util.Date;

public abstract class Data implements IData
{
    private final DataType type;
    private final Date date;

    public Data(DataType type, Date date)
    {
        this.type = type;
        this.date = date;
    }

    /**
     * Whether this instance is a duplicate instance of another Data object.
     *
     * @param other
     * @return
     */
    public abstract boolean isDuplicate(Data other);

    @Override
    public DataType getType()
    {
        return type;
    }

    @Override
    public Date getDate()
    {
        return date;
    }

    @Override
    public boolean isProcessed()
    {
        return true;
    }

}
