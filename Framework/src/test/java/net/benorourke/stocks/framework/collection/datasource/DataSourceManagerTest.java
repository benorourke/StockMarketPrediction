package net.benorourke.stocks.framework.collection.datasource;

import net.benorourke.stocks.framework.collection.Query;
import net.benorourke.stocks.framework.collection.constraint.Constraint;
import net.benorourke.stocks.framework.collection.session.CollectionSession;
import net.benorourke.stocks.framework.collection.session.filter.CollectionFilter;
import net.benorourke.stocks.framework.exception.ConstraintException;
import net.benorourke.stocks.framework.exception.FailedCollectionException;
import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;
import net.benorourke.stocks.framework.util.Initialisable;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DataSourceManagerTest implements Initialisable
{
    private static final String DUMMY_NAME = "Dummy";

    private DataSourceManager manager;
    private DummySource dummy;

    @Before
    @Override
    public void initialise()
    {
        manager = new DataSourceManager();
        manager.getDataSources().add(dummy = new DummySource(DUMMY_NAME));
    }

    @Test
    public void getDataSourceByClass_ByDummyClass_ShouldBeNotNull()
    {
        DataSource src = manager.getDataSourceByClass(DummySource.class);

        assertNotEquals(null, src);
    }

    @Test
    public void getDataSourceByClass_ByDummyClass_ShouldBeSameInstance()
    {
        DataSource src = manager.getDataSourceByClass(DummySource.class);

        assertEquals(dummy, src);
    }

    @Test
    public void getDataSourceByName_ByDummyName_ShouldBeNotNull()
    {
        DataSource src = manager.getDataSourceByName(DUMMY_NAME);

        assertNotEquals(null, src);
    }

    @Test
    public void getDataSourceByName_ByDummyName_ShouldBeSameInstance()
    {
        DataSource src = manager.getDataSourceByName(DUMMY_NAME);

        assertEquals(dummy, src);
    }

    private class DummySource extends DataSource<Data>
    {

        public DummySource(String name)
        {
            super(name);
        }

        @Override
        public Class<? extends Data> getDataClass()
        {
            return StockQuote.class;
        }

        @Override
        public DataType getDataType()
        {
            return DataType.STOCK_QUOTE;
        }

        @Override
        public Constraint[] getConstraints()
        {
            return new Constraint[0];
        }

        @Override
        public CollectionSession<Data> newSession(Query completeQuery, CollectionFilter<Data> collectionFilter)
        {
            return null;
        }

        @Override
        public CollectionFilter<Data> newDefaultCollectionFilter()
        {
            return data -> false;
        }

        @Override
        public Collection<Data> retrieve(Query query) throws ConstraintException, FailedCollectionException
        {
            return new ArrayList<>();
        }

    }

}
