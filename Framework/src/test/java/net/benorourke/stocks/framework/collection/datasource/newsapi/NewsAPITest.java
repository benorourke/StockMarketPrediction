package net.benorourke.stocks.framework.collection.datasource.newsapi;

import net.benorourke.stocks.framework.collection.datasource.alphavantage.AlphaVantage;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class NewsAPITest
{

    @Test
    public void getCollectionVariables_OnceConstructed_ShouldContain2()
    {
        NewsAPI source = new NewsAPI();
        assertEquals(source.getCollectionVariables().size(), 3);
    }

    @Test
    public void getCollectionVariables_OnceConstructed_ShouldMatchVariables()
    {
        NewsAPI source = new NewsAPI();
        List<String> actual = source.getCollectionVariables()
                                                    .stream()
                                                    .map(v -> v.name())
                                                    .collect(Collectors.toList());
        List<String> expected = Arrays.asList("API Key", "Search Term", "Headlines per Day");
        assertThat(expected, is(actual));
    }

}
