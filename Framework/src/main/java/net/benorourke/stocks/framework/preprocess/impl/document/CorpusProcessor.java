package net.benorourke.stocks.framework.preprocess.impl.document;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.preprocess.Preprocess;
import net.benorourke.stocks.framework.series.data.impl.CleanedDocument;
import net.benorourke.stocks.framework.series.data.impl.ProcessedDocument;
import net.benorourke.stocks.framework.series.data.impl.ProcessedStockQuote;

import java.util.*;

public class CorpusProcessor extends Preprocess<Map<Date, List<CleanedDocument>>, ProcessedCorpus>
{
    private static final int PROGRESS_ITERATIONS = 50;

    private final Map<Date, ProcessedStockQuote> labels;
    private StanfordCoreNLP pipeline;

    public CorpusProcessor(Map<Date, ProcessedStockQuote> labels)
    {
        this.labels = labels;
    }

    @Override
    public void initialise()
    {
        Framework.info("Creating Pipeline [Processing]");
        pipeline = new StanfordCoreNLP(createProperties());
        Framework.info("Created Pipeline [Processing]");
    }

    @Override
    public ProcessedCorpus preprocess(Map<Date, List<CleanedDocument>> data)
    {
        ProcessedCorpus corpus = new ProcessedCorpus(new HashMap<>());
        Map<Date, List<ProcessedDocument>> docs = corpus.getDocuments();

        for (Map.Entry<Date, List<CleanedDocument>> entry : data.entrySet())
        {
            for (CleanedDocument doc : entry.getValue())
            {

            }
        }


        int total = data.size(), count = 0;
//        for (CleanedDocument document : data)
//        {
//            processed.add(process(document));
//
//            count ++;
//            if(count % PROGRESS_ITERATIONS == 0)
//                onProgressChanged(( (double) count / (double) total) * 100 );
//        }
        onProgressChanged(100.0D);
        return corpus;
    }

    public ProcessedDocument process(CleanedDocument document)
    {
        LinkedHashMap<String, Sentiment> map = new LinkedHashMap<>();
        final Annotation doc = new Annotation(document.getCleanedContent());
        pipeline.annotate(doc);
        for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class))
        {
            final String strSentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
            Sentiment sentiment = Sentiment.values()[RNNCoreAnnotations.getPredictedClass(tree)];

            Framework.debug("Sentiment for " + sentence + ": " + sentiment);
        }

        return null;
    }

    private Properties createProperties()
    {
        Properties props = new Properties();
        String annotators = "tokenize, ssplit, pos, lemma, parse, sentiment";
        props.put("annotators", annotators);
        return props;
    }

}
