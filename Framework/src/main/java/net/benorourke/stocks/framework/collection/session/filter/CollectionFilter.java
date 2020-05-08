package net.benorourke.stocks.framework.collection.session.filter;

import net.benorourke.stocks.framework.series.data.Data;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface CollectionFilter<T extends Data>
{

    /**
     * Whether or not to discard a piece of data that has been collected.
     *
     * @param data
     * @return
     */
    boolean discard(T data);

    static <T extends Data> List<T> reduce(Collection<T> data, CollectionFilter<T> filter)
    {
        return data.stream()
                   .filter(d -> !filter.discard(d))
                   .collect(Collectors.toList());
    }

}
