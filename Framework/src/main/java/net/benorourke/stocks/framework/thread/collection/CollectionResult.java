package net.benorourke.stocks.framework.thread.collection;

import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.thread.Result;

import java.util.ArrayList;
import java.util.List;

public class CollectionResult<T extends Data> extends Result
{
    private final List<T> data;

    public CollectionResult()
    {
        data = new ArrayList<T>();
    }

    public List<T> getData()
    {
        return data;
    }

}
