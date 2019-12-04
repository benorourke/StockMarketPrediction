package net.benorourke.nlp.mining;

import java.util.ArrayList;
import java.util.List;

public class BagOfWords {

    /**
     * A measure of the significance of a word in the entire corpus.
     *
     * Returns 0 for a word found in all of the documents; bearing little significance.
     *
     * @param corpus
     * @param doc
     * @param term
     * @return
     */
    public double tfidf(Document[] corpus, Document doc, String term) {
        return termFrequency(doc, term) * inverseDocumentFrequency(corpus, term);
    }

    public double termFrequency(Document doc, String term) {
        double freq = 0;
        for (String word : doc.getWords()) {
            if (word.equals(term)) freq ++;
            else System.out.println(word + " != " + term);
        }
        return freq / doc.size();
    }

    public double inverseDocumentFrequency(Document[] corpus, String term) {
        double freq = 0;
        for (Document doc : corpus) {
            inner: for (String word : doc.getWords()) {
                if (term.equalsIgnoreCase(word)) {
                    freq ++;
                    break inner;
                }
            }
        }
        return Math.log((double) corpus.length / freq);
    }

}
