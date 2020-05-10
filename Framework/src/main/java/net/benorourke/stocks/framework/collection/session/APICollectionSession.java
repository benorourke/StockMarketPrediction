package net.benorourke.stocks.framework.collection.session;

import net.benorourke.stocks.framework.collection.session.filter.CollectionFilter;
import net.benorourke.stocks.framework.series.data.Data;

public abstract class APICollectionSession<T extends Data> extends CollectionSession<T>
{

    public APICollectionSession(CollectionFilter<T> collectionFilter)
    {
        super(collectionFilter);
    }

}
