package net.benorourke.stocks.framework.collection.datasource.alphavantage;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class AlphaVantageTest
{

    @Test
    public void getCollectionVariables_OnceConstructed_ShouldContain2()
    {
        AlphaVantage source = new AlphaVantage();
        assertEquals(source.getCollectionVariables().size(), 2);
    }

    @Test
    public void getCollectionVariables_OnceConstructed_ShouldMatchVariables()
    {
        AlphaVantage source = new AlphaVantage();
        List<String> actual = source.getCollectionVariables()
                                                    .stream()
                                                    .map(v -> v.name())
                                                    .collect(Collectors.toList());
        List<String> expected = Arrays.asList("API Key", "Stock Symbol");
        assertThat(expected, is(actual));
    }

}
