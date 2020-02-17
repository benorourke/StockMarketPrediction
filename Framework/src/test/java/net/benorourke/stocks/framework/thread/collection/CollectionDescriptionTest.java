package net.benorourke.stocks.framework.thread.collection;

import net.benorourke.stocks.framework.collection.datasource.alphavantage.AlphaVantage;
import net.benorourke.stocks.framework.collection.datasource.newsapi.NewsAPI;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CollectionDescriptionTest
{

    @Test
    public void equalsMethod_DifferingInstancesSameSource_ShouldReturnTrue()
    {
        CollectionDescription desc1 = new CollectionDescription(NewsAPI.class);
        CollectionDescription desc2 = new CollectionDescription(NewsAPI.class);

        assertEquals(true, desc1.equals(desc2));

        CollectionDescription desc3 = new CollectionDescription(AlphaVantage.class);
        CollectionDescription desc4 = new CollectionDescription(AlphaVantage.class);

        assertEquals(true, desc3.equals(desc4));
    }

}
