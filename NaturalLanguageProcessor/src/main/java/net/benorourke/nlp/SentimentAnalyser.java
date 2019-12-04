package net.benorourke.nlp;

import java.util.LinkedHashMap;
import java.util.Map;

public interface SentimentAnalyser {

    /**
     * Analyse a documents sentiments across its individual sentences.
     *
     * @param document the document to analyse
     * @return a Map of (Sentence, Sentiment) Key, Value pairs
     */
    LinkedHashMap<String, Double> analyse(String document);

}
