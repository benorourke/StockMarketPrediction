package net.benorourke.stocks.framework.preprocess.impl.document.relevancy;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.series.data.impl.CleanedDocument;
import org.deeplearning4j.earlystopping.saver.LocalFileGraphSaver;

import java.util.*;

public class TF_IDF implements RelevancyMetric
{

    public enum Logarithm {BASE_10, NATURAL};

    private final Logarithm logarithm;
    private final List<CleanedDocument> corpus;
    /**
     * The Map of terms against the number of documents they appear in.
     * <p>
     * IDF is constant per corpus.
     */
    private final Map<String, Integer> idfMap;

    private double corpusSize;

    public TF_IDF(Logarithm logarithm)
    {
        this.logarithm = logarithm;
        corpus = new ArrayList<>();
        idfMap = new LinkedHashMap<>();
    }

    @Override
    public void initialise(Map<Date, List<CleanedDocument>> data)
    {
        corpus.clear();
        idfMap.clear();

        // Create the map of terms against the number of documents where a term appears
        corpus.addAll(collateDocuments(data));
        corpusSize = (double) corpus.size();

        for (CleanedDocument document : corpus)
        {
            HashSet<String> termsSeenThisDocument = new HashSet<>();

            inner:
            for (String term : document.getCleanedTerms())
            {
                // Don't want to increment this term in the idfMap if we already have
                // for this document
                if (!termsSeenThisDocument.contains(term))
                {
                    termsSeenThisDocument.add(term);

                    if (!idfMap.containsKey(term))
                        idfMap.put(term, 0);

                    idfMap.put(term, idfMap.get(term) + 1);
                }
            }
        }
    }

    @Override
    public List<String> getMostRelevant(int maximumCount)
    {
        List<ScoredTerm> terms = new ArrayList<>();
        for (String term : getLexicon())
        {
            terms.add(new ScoredTerm(term, averageTFIDF(term)));
        }
        // Sort descending
        terms.sort( ((o1, o2) -> (o1.averageTFIDF > o2.averageTFIDF)
                                        ? -1
                                        : (o1.averageTFIDF < o2.averageTFIDF
                                                ? 1
                                                : 0)));

        for (ScoredTerm term : terms)
        {
            Framework.info(term.term + ": " + term.averageTFIDF);
        }

        List<String> mostRelevant = new ArrayList<>();
        if (!terms.isEmpty())
        {
            for (ScoredTerm term : terms.subList(0, Math.min(terms.size(), maximumCount) - 1))
            {
                mostRelevant.add(term.term);
            }
        }

        return mostRelevant;
    }

    private List<CleanedDocument> collateDocuments(Map<Date, List<CleanedDocument>> data)
    {
        List<CleanedDocument> collated = new ArrayList<>();
        for (List<CleanedDocument> value : data.values())
        {
            collated.addAll(value);
        }
        return collated;
    }

    public double termFrequency(String term, CleanedDocument document)
    {
        int appearances = (int) document.getCleanedTerms()
                                        .stream()
                                        .filter(string -> string.equals(term))
                                        .count();
        return (double) appearances / (double) document.getCleanedTerms().size();
    }

    public double inverseDocumentFrequency(String term)
    {
        double denominator = (double) idfMap.get(term); // Can add 1, but a term should never be missings
        return log( corpusSize / (denominator) );
    }

    public double tfidf(String term, CleanedDocument document)
    {
        return termFrequency(term, document) * inverseDocumentFrequency(term);
    }

    public double averageTFIDF(String term)
    {
        double sum = 0;
        for (CleanedDocument document : corpus)
        {
            sum += tfidf(term, document);
        }
        return sum / corpusSize;
    }

    private double log(double a)
    {
        switch (logarithm)
        {
            case BASE_10:
                return Math.log10(a);
            case NATURAL:
            default:
                return Math.log(a);
        }
    }

    /**
     * Get every possible term in the dictionary
     * @return
     */
    private final Set<String> getLexicon()
    {
        HashSet<String> lexicon = new HashSet<>();
        for (CleanedDocument document : corpus)
        {
            lexicon.addAll(document.getCleanedTerms());
        }
        return lexicon;
    }

    private class ScoredTerm {
        private String term;
        private double averageTFIDF;

        private ScoredTerm(String term, double averageTFIDF)
        {
            this.term = term;
            this.averageTFIDF = averageTFIDF;
        }

    }

}
