package net.benorourke.stocks.framework.preprocess.quote;

import net.benorourke.stocks.framework.series.data.impl.StockQuote;
import net.benorourke.stocks.framework.series.data.impl.StockQuoteDataType;
import net.benorourke.stocks.framework.util.Initialisable;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class StockQuoteFeatureRepresentorTest implements Initialisable
{
    private static final Random RANDOM = new Random();

    private StockQuoteFeatureRepresentor featureRepresentor;

    @Before
    @Override
    public void initialise()
    {
        featureRepresentor = new StockQuoteFeatureRepresentor(StockQuoteDataType.values());
        featureRepresentor.initialise(new ArrayList<>());
    }

    @Test
    public void preprocess_RandomDataValues_ShouldMatchValues()
    {
        double[] expected = new double[StockQuoteDataType.values().length];
        for (int i = 0; i < expected.length; i ++)
            expected[i] = RANDOM.nextDouble();

        StockQuote quote = new StockQuote(new Date(), expected[0], expected[1], expected[2], expected[3], expected[4]);
        double[] actual = featureRepresentor.getVectorRepresentation(quote);
        assertThat(expected, is(actual));
    }

}
