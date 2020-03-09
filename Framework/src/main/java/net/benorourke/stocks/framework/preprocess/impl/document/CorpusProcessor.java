package net.benorourke.stocks.framework.preprocess.impl.document;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.model.ModelData;
import net.benorourke.stocks.framework.model.ProcessedCorpus;
import net.benorourke.stocks.framework.preprocess.Preprocess;
import net.benorourke.stocks.framework.preprocess.impl.document.relevancy.RelevancyMetric;
import net.benorourke.stocks.framework.preprocess.impl.document.relevancy.TF_IDF;
import net.benorourke.stocks.framework.series.data.DatasetHelper;
import net.benorourke.stocks.framework.series.data.impl.CleanedDocument;
import net.benorourke.stocks.framework.series.data.impl.ProcessedDocument;
import net.benorourke.stocks.framework.series.data.impl.NormalisedStockQuote;
import net.benorourke.stocks.framework.util.DateUtil;

import java.util.*;

// TODO - Progress
public class CorpusProcessor extends Preprocess<Map<Date, List<CleanedDocument>>, ProcessedCorpus>
{
    private final Map<Date, NormalisedStockQuote> labels;
    private final RelevancyMetric relevancyMetric;
    private final int relevantTermCount;

    private StanfordCoreNLP pipeline;

    public CorpusProcessor(Map<Date, NormalisedStockQuote> labels,
                           RelevancyMetric termRelevancyMetric, int relevantTermCount)
    {
        this.labels = labels;
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

        Framework.info("[RelevancyMetric] Initialising " + relevancyMetric.getClass().getSimpleName());
        relevancyMetric.initialise(data);
        List<String> topTerms = relevancyMetric.getMostRelevant(relevantTermCount);
        Framework.info("[RelevancyMetric] Initialised " + relevancyMetric.getClass().getSimpleName());
        Framework.info("[RelevancyMetric] Found " + topTerms.size() + " Terms");

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

        // TODO: Delete from here down and replace with working
        Framework.debug("Creating fake corpus");
        ProcessedCorpus corpus = new ProcessedCorpus();
        int idx = 1;
        for (Map.Entry<Date, NormalisedStockQuote> entry : labels.entrySet())
        {
            Framework.debug("Added " + (idx ++) + entry.getKey().toString());

            double[] features = new double[ModelData.N_FEATURES];
            double[] labels = new double[ModelData.N_LABELS];

            corpus.getData().add(new ModelData(DateUtil.getDayStart(entry.getKey()),
                                               entry.getValue().getNormalisedData(),
                                               entry.getValue().getUnnormalisedData()));
        }

        onProgressChanged(100.0D);
        Framework.debug("Created fake corpus");
        return corpus;
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
//        Framework.info("Mode sentiment for '" + document.getOriginalContent() + "': " + mode);
        return new ProcessedDocument(document.getDate(), mode);
    }

}
