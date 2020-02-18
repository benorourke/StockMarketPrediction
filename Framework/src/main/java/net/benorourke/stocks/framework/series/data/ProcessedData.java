package net.benorourke.stocks.framework.series.data;

import java.util.Date;

public class ProcessedData extends Data
{

    public ProcessedData(DataType type, Date date)
    {
        super(type, date);
    }

    @Override
    public boolean isProcessed()
    {
        return true;
    }

}
