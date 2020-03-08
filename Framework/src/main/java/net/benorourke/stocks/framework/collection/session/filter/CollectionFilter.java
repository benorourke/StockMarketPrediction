package net.benorourke.stocks.framework.collection.session.filter;

import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.impl.Document;

public interface CollectionFilter<T extends Data>
{

    /**
     * Whether or not to discard a piece of data that has been collected.
     *
     * @param data
     * @return
     */
    boolean discard(T data);

}
