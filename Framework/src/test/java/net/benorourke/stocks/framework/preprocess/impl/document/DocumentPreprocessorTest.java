package net.benorourke.stocks.framework.preprocess.impl.document;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.series.data.DocumentType;
import net.benorourke.stocks.framework.series.data.impl.Document;
import net.benorourke.stocks.framework.series.data.impl.CleanedDocument;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DocumentPreprocessorTest
{

    // TODO - Refine this / remove this - it's annoying to run tests for so long
    @Test
    public void rename()
    {
        DocumentCleaner preprocessor = new DocumentCleaner();
        preprocessor.initialise();

        List<Document> corpus = new ArrayList<Document>();
        corpus.add(new Document(new Date(), "The quick brown fox jumps over the lazy dog", DocumentType.NEWS_HEADLINE));

        List<CleanedDocument> res = preprocessor.preprocess(corpus);
        CleanedDocument elem0 = res.get(0);

        Framework.debug("Res: " + elem0.getCleanedContent());
    }

}
