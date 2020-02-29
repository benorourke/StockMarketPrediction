package net.benorourke.stocks.framework.preprocess.impl.document;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.model.data.ProcessedCorpus;
import net.benorourke.stocks.framework.preprocess.Preprocess;
import net.benorourke.stocks.framework.series.data.DatasetHelper;
import net.benorourke.stocks.framework.series.data.impl.CleanedDocument;
import net.benorourke.stocks.framework.series.data.impl.ProcessedDocument;
import net.benorourke.stocks.framework.series.data.impl.NormalisedStockQuote;

import java.util.*;

public class CorpusProcessor extends Preprocess<Map<Date, List<CleanedDocument>>, ProcessedCorpus>
{
    private static final int PROGRESS_ITERATIONS = 50;

    private final Map<Date, NormalisedStockQuote> labels;
    private StanfordCoreNLP pipeline;

    public CorpusProcessor(Map<Date, NormalisedStockQuote> labels)
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
        Framework.debug("ModelData size: " + data.size());

        // TODO: Update Percentages
        Map<Date, List<ProcessedDocument>> docs = new HashMap<>();

        Framework.info("Combining Saturday/Sunday Documents with Monday");
        int dataSizePreCombination = data.size();
        data = DatasetHelper.handleWeekends(data);
        Framework.info("Combined Saturday/Sunday Documents with Monday ("
                            + (dataSizePreCombination - data.size()) + " weekend days removed)");

        Framework.info("TODO: Generating TF_IDF");
        TF_IDF tf_idf = TF_IDF.generate(data);
        Framework.info("TODO: Generated TF_IDF");

        Framework.debug("ModelData size2: " + data.size());
        Framework.info("Processing individual documents");
        for (Map.Entry<Date, List<CleanedDocument>> entry : data.entrySet())
        {
            List<ProcessedDocument> list = new ArrayList<>();
            docs.put(entry.getKey(), list);
            for (CleanedDocument doc : entry.getValue())
            {
                list.add(process(doc));
            }
        }
        Framework.info("Processed individual documents");

        onProgressChanged(100.0D);
//        return new ProcessedCorpus(docs);
        return new ProcessedCorpus(new ArrayList<>());
    }

    public ProcessedDocument process(CleanedDocument document)
    {
        final Annotation doc = new Annotation(document.getOriginalContent());
        pipeline.annotate(doc);

        // Take the total sums of Sentiments across the sentences
        Map<Sentiment, Integer> sentimentCounts = new HashMap<>();
        for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class))
        {
            String nlpClass = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            Sentiment sentiment = Sentiment.fromStanfordClass(nlpClass);

            if (sentimentCounts.containsKey(sentiment))
                sentimentCounts.put(sentiment, sentimentCounts.get(sentiment) + 1);
            else
                sentimentCounts.put(sentiment, 1);
        }

        // Determine the mode Sentiment
        Sentiment mode = Sentiment.NEUTRAL;
        int cardinality = 0;
        for (Map.Entry<Sentiment, Integer> entry : sentimentCounts.entrySet())
        {
            if (entry.getValue() > cardinality)
            {
                mode = entry.getKey();
                cardinality = entry.getValue();
            }
        }
        Framework.info("Mode sentiment for '" + document.getOriginalContent() + "': " + mode);
        return new ProcessedDocument(document.getDate(), document.getCleanedContent(), mode);
    }

    private Properties createProperties()
    {
        Properties props = new Properties();
        String annotators = "tokenize, ssplit, pos, lemma, parse, sentiment";
        props.put("annotators", annotators);
        return props;
    }

}
