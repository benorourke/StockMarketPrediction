package net.benorourke.stocks.framework.collection.session.filter;

import net.benorourke.stocks.framework.series.data.Data;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A filter that can be applied on a collection in order to reduce it.
 *
 * @param <T> the type of data to reduce.
 */
public interface CollectionFilter<T extends Data>
{

    /**
     * Whether or not to discard a piece of data that has been collected. (True = discard)
     *
     * @param data the element
     * @return whether to discard
     */
    boolean discard(T data);

    /**
     * Reduce a dataset based on a filter.
     *
     * @param data the dataset
     * @param filter the filter
     * @param <T> the inferred type of data
     * @return the reduced dataset.
     */
    static <T extends Data> List<T> reduce(Collection<T> data, CollectionFilter<T> filter)
    {
        return data.stream()
                   .filter(d -> !filter.discard(d))
                   .collect(Collectors.toList());
    }

}
