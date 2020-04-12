package net.benorourke.stocks.framework.preprocess.assignment;

import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.DataType;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class IgnorePolicy implements MissingDataPolicy
{

    @Override
    public void handle(Map<Date, List<Data>> data, Map<Date, List<DataType>> missingTypes)
    {
        for (Date key : missingTypes.keySet())
            data.remove(key);
    }

}
