package net.benorourke.stocks.framework.preprocess.document;

import net.benorourke.stocks.framework.series.data.DocumentType;
import net.benorourke.stocks.framework.series.data.impl.Document;
import net.benorourke.stocks.framework.util.Initialisable;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class DimensionalityReductionTest implements Initialisable
{
    private DimensionalityReduction dimensionalityReduction;

    @Before
    @Override
    public void initialise()
    {
        dimensionalityReduction = new DimensionalityReduction();
        dimensionalityReduction.initialise();
    }

    @Test
    public void preprocess_ContainsURL_ShouldBeRemoved()
    {
        String content = "test http://www.google.com/";

        Document document = new Document(new Date(), content, DocumentType.TWEET);
        List<String> expected = Arrays.asList("test");
        List<String> actual = dimensionalityReduction.preprocess(Arrays.asList(document)).get(0).getCleanedTerms();
        assertThat(expected, is(actual));
    }

    @Test
    public void preprocess_VerbForms_ShouldBeStemmed()
    {
        String content = "test tests testing tested";

        Document document = new Document(new Date(), content, DocumentType.NEWS_HEADLINE);
        List<String> expected = Arrays.asList("test", "test", "test", "test");
        List<String> actual = dimensionalityReduction.preprocess(Arrays.asList(document)).get(0).getCleanedTerms();
        assertThat(expected, is(actual));
    }

    @Test
    public void preprocess_ContainsPunctuation_ShouldBeRemoved()
    {
        String content = "test. tests! testing, tested?";

        Document document = new Document(new Date(), content, DocumentType.NEWS_HEADLINE);
        List<String> expected = Arrays.asList("test", "test", "test", "test");
        List<String> actual = dimensionalityReduction.preprocess(Arrays.asList(document)).get(0).getCleanedTerms();
        assertThat(expected, is(actual));
    }

    @Test
    public void preprocess_ContainsNumbers_ShouldBeRemoved()
    {
        String content = "test 12356 tests4 testing27 tested45";

        Document document = new Document(new Date(), content, DocumentType.NEWS_HEADLINE);
        List<String> expected = Arrays.asList("test", "test", "test", "test");
        List<String> actual = dimensionalityReduction.preprocess(Arrays.asList(document)).get(0).getCleanedTerms();
        assertThat(expected, is(actual));
    }

}
