package net.benorourke.stocks.framework.preprocessor.impl.document;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.preprocessor.stanford.StanfordPreprocessor;
import net.benorourke.stocks.framework.series.data.impl.Document;
import net.benorourke.stocks.framework.series.data.impl.ProcessedDocument;

import java.util.List;
import java.util.Properties;

public class DocumentPreprocessor implements net.benorourke.stocks.framework.preprocessor.Preprocessor<Document, ProcessedDocument>
{
    private enum Preprocess
    {
        /**
         * Lemmatise & remove stop words.
         */
        CLEAN,
    };

    private StanfordCoreNLP pipeline;

    @Override
    public void initialise()
    {
        Framework.info("Creating Stanford CoreNLP Pipeline");
        pipeline = new StanfordCoreNLP(createProperties());
        Framework.info("Created Stanford CoreNLP Pipeline");
    }

    @Override
    public List<ProcessedDocument> preprocess(List<Document> data)
    {
        for (Document document : data)
        {

        }
        return null;
    }

    private Properties createProperties()
    {
        Properties props = new Properties();
        String annotators = "tokenize, ssplit, pos, lemma, stopword";
        props.setProperty("customAnnotatorClass.stopword", StopwordAnnotator.class.getName());
        props.put("annotators", annotators);
        return props;
    }

}
