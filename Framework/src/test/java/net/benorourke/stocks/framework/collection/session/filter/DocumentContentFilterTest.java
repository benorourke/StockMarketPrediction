package net.benorourke.stocks.framework.collection.session.filter;

import net.benorourke.stocks.framework.series.data.DocumentType;
import net.benorourke.stocks.framework.series.data.impl.Document;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DocumentContentFilterTest
{
    private static final DocumentContentFilter CASE_INSENSITIVE =
            new DocumentContentFilter(Arrays.asList("word1", "word2"), true);

    private static final Date TODAY = new Date();

    @Test
    public void discard_NoMatches_ShouldBeEmpty()
    {
        List<Document> documents = new ArrayList<>();

        // Add documents that do not contain the words [word1, word2]
        for (int i = 3; i < 10; i ++)
            documents.add(new Document(TODAY, "word" + i, DocumentType.NEWS_HEADLINE));

        List<Document> actual = CollectionFilter.reduce(documents, CASE_INSENSITIVE);
        assertEquals(actual.isEmpty(), true);
    }

}
