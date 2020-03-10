package net.benorourke.stocks.framework.preprocess.document;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.model.ModelData;
import net.benorourke.stocks.framework.model.ProcessedCorpus;
import net.benorourke.stocks.framework.preprocess.Preprocess;
import net.benorourke.stocks.framework.preprocess.document.relevancy.RelevancyMetric;
import net.benorourke.stocks.framework.series.data.DatasetHelper;
import net.benorourke.stocks.framework.series.data.impl.CleanedDocument;
import net.benorourke.stocks.framework.series.data.impl.ProcessedDocument;
import net.benorourke.stocks.framework.series.data.impl.NormalisedStockQuote;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;
import net.benorourke.stocks.framework.util.DateUtil;

import java.util.*;

// TODO - Progress
// TODO - Create SentimentAnalyzer object for this
public class FeatureRepresenter extends Preprocess< Map<Date, List<CleanedDocument>>,
                                                            Map<Date, List<ProcessedDocument>> >
{
    private final RelevancyMetric relevancyMetric;
    private final int relevantTermCount;

    private StanfordCoreNLP pipeline;

    public FeatureRepresenter(RelevancyMetric termRelevancyMetric, int relevantTermCount)
    {
        this.relevancyMetric = termRelevancyMetric;
        this.relevantTermCount = relevantTermCount;
    }

    @Override
    public void initialise()
    {
        Framework.info("Creating Pipeline [Processing]");

        Properties props = new Properties();
        String annotators = "tokenize, ssplit, pos, lemma, parse, sentiment";
        props.put("annotators", annotators);
        pipeline = new StanfordCoreNLP(props);

        Framework.info("Created Pipeline [Processing]");
    }

    @Override
    public Map<Date, List<ProcessedDocument>> preprocess(Map<Date, List<CleanedDocument>> data)
    {
        Map<Date, List<ProcessedDocument>> documents = new HashMap<>();

        Framework.info("[Weekend Removal (1/3)] Combining Saturday/Sunday Documents with Monday");
        int dataSizePreCombination = data.size();
        Map<Date, List<CleanedDocument>> weekendsRemoved = DatasetHelper.handleWeekends(data);
        Framework.info("[Weekend Removal (1/3)] Combined Saturday/Sunday Documents with Monday ("
                            + (dataSizePreCombination - data.size()) + " weekend days removed)");

        Framework.info("[Top Term Metric (2/3)] Initialising " + relevancyMetric.getClass().getSimpleName());
        relevancyMetric.initialise(weekendsRemoved);
        String[] topTerms = relevancyMetric.getMostRelevant(relevantTermCount);
        Framework.info("[Top Term Metric (2/3)] Initialised " + relevancyMetric.getClass().getSimpleName());
        Framework.info("[Top Term Metric (2/3)] Found " + topTerms.length
                                + " Terms (" + String.join(",", topTerms) + ")");

        Framework.info("[Document Processing (3/3)] Processing Individual Documents");
        for (Map.Entry<Date, List<CleanedDocument>> entry : weekendsRemoved.entrySet())
        {
            List<ProcessedDocument> list = new ArrayList<>();
            documents.put(entry.getKey(), list);
            for (CleanedDocument doc : entry.getValue())
            {
                list.add(process(doc, topTerms));
            }
        }
        Framework.info("[Document Processing (3/3)] Processed Individual Documents");

        return documents;
    }

    public ProcessedDocument process(CleanedDocument document, String[] topTerms)
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

        boolean[] topTermVector = topTermVector(topTerms, document);

        // DEBUG BELOW
        String[] topTermVectorStrings = new String[topTermVector.length];
        for (int i = 0; i < topTermVector.length; i ++)
        {
            topTermVectorStrings[i] = topTermVector[i] ? "1" : "0";
        }
        Framework.debug("[Document Processing (3/5)] "
                            + String.join(",", document.getCleanedTerms())
                            + " -> [" + String.join(",", topTermVectorStrings) + "]");
        // DEBUG ABOVE

        return new ProcessedDocument(document.getDate(), mode, topTermVector);
    }

    public boolean[] topTermVector(String[] topTerms, CleanedDocument document)
    {
        boolean[] vector = new boolean[topTerms.length];
        Arrays.fill(vector, false);

        outer: for (int i = 0; i < topTerms.length; i ++)
        {
            String topTerm = topTerms[i];

            for (String token : document.getCleanedTerms())
            {
                if (topTerm.equals(token))
                {
                    vector[i] = true;
                    continue outer;
                }
            }
        }

        return vector;
    }

}
