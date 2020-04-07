package net.benorourke.stocks.framework.preprocess.document.representer.sentiment;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.preprocess.FeatureRepresenter;
import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.impl.CleanedDocument;
import net.benorourke.stocks.framework.util.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SentimentFeatureRepresenter implements FeatureRepresenter<CleanedDocument>
{
    @Nullable
    private StanfordCoreNLP pipeline;

    @Override
    public DataType<CleanedDocument> getTypeFor()
    {
        return DataType.CLEANED_DOCUMENT;
    }

    @Override
    public void initialise(List<CleanedDocument> corpus)
    {
        Framework.info("[SentimentFeatureRepresenter] Creating Pipeline [Processing]");

        Properties props = new Properties();
        String annotators = "tokenize, ssplit, pos, lemma, parse, sentiment";
        props.put("annotators", annotators);
        pipeline = new StanfordCoreNLP(props);

        Framework.info("[SentimentFeatureRepresenter] Created Pipeline [Processing]");
    }

    @Override
    public int getVectorSize()
    {
        return Sentiment.values().length;
    }

    @Override
    public double[] getVectorRepresentation(CleanedDocument document)
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

        return mode.toInputVector();
    }

    @Override
    public String getName()
    {
        return "Sentiment";
    }

    @Override
    public CombinationPolicy getCombinationPolicy()
    {
        return CombinationPolicy.TAKE_MODE_AVERAGE; // TODO - check whether we should use mode?
    }

}
