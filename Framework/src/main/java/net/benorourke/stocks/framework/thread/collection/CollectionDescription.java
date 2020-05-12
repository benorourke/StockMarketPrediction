package net.benorourke.stocks.framework.thread.collection;

import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.thread.TaskDescription;
import net.benorourke.stocks.framework.thread.TaskType;

/**
 * The object to generate collisions when queueing tasks. We will allow concurrent collection tasks, however
 * disallow concurrent collection tasks collecting from the same data source.
 */
public class CollectionDescription extends TaskDescription
{
    private final Class<? extends DataSource> dataSourceClazz;

    public CollectionDescription(Class<? extends DataSource> dataSourceClazz)
    {
        super(TaskType.COLLECTION);

        this.dataSourceClazz = dataSourceClazz;
    }

    @Override
    public boolean equals(Object object)
    {
        return object instanceof CollectionDescription
                && ((CollectionDescription) object).dataSourceClazz.equals(dataSourceClazz);
    }

    public Class<? extends DataSource> getDataSourceClazz()
    {
        return dataSourceClazz;
    }

}
