package net.benorourke.stocks.framework.preprocessor.impl.document;

import java.util.LinkedHashMap;

public interface SentimentAnalyser
{

    /**
     * Analyse a documents sentiments across its individual sentences.
     *
     * @param document the document to analyse
     * @return a Map of (Sentence, Sentiment) Key, Value pairs
     */
    LinkedHashMap<String, Double> analyse(String document);

}
