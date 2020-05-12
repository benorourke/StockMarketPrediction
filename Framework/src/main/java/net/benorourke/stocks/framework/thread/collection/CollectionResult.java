package net.benorourke.stocks.framework.thread.collection;

import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.thread.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * The result for collection tasks.
 *
 * @param <T> the inferred type of data
 */
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
