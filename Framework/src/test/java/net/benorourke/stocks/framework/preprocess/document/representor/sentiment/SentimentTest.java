package net.benorourke.stocks.framework.preprocess.document.representor.sentiment;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SentimentTest
{

    @Test
    public void fromStanfordClass_Stanford_EqualsVERY_NEGATIVE()
    {
        assertEquals(Sentiment.VERY_NEGATIVE, Sentiment.fromStanfordClass("VERY NEGATIVE"));
    }

    @Test
    public void fromStanfordClass_Stanford_EqualsNEGATIVE()
    {
        assertEquals(Sentiment.NEGATIVE, Sentiment.fromStanfordClass("NEGATIVE"));
    }

    @Test
    public void fromStanfordClass_Stanford_EqualsNEUTRAL()
    {
        assertEquals(Sentiment.NEUTRAL, Sentiment.fromStanfordClass("NEUTRAL"));
    }

    @Test
    public void fromStanfordClass_Stanford_EqualsPOSITIVE()
    {
        assertEquals(Sentiment.POSITIVE, Sentiment.fromStanfordClass("POSITIVE"));
    }

    @Test
    public void fromStanfordClass_Stanford_EqualsVERY_POSITIVE()
    {
        assertEquals(Sentiment.VERY_POSITIVE, Sentiment.fromStanfordClass("VERY_POSITIVE"));
    }

}
