package net.benorourke.stocks.framework.preprocess.assignment;

import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.DataType;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface MissingDataPolicy
{

    /**
     *
     * @param data (contains a key for every date within missingTypes)
     * @param missingTypes
     */
    void handle(Map<Date, List<Data>> data, Map<Date, List<DataType>> missingTypes);

}
