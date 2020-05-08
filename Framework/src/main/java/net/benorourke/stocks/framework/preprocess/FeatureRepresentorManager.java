package net.benorourke.stocks.framework.preprocess;

import net.benorourke.stocks.framework.preprocess.assignment.IgnorePolicy;
import net.benorourke.stocks.framework.preprocess.assignment.MissingDataPolicy;
import net.benorourke.stocks.framework.preprocess.document.representor.sentiment.BinarySentimentFeatureRepresentor;
import net.benorourke.stocks.framework.preprocess.document.representor.sentiment.NormalisedSentimentFeatureRepresentor;
import net.benorourke.stocks.framework.preprocess.document.representor.sentiment.Sentiment;
import net.benorourke.stocks.framework.preprocess.document.representor.topterm.TF_IDF;
import net.benorourke.stocks.framework.preprocess.document.representor.topterm.TopTermFeatureRepresentor;
import net.benorourke.stocks.framework.preprocess.quote.StockQuoteFeatureRepresentor;
import net.benorourke.stocks.framework.series.data.impl.CleanedDocument;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;
import net.benorourke.stocks.framework.series.data.impl.StockQuoteDataType;
import net.benorourke.stocks.framework.util.Initialisable;

import java.util.*;
import java.util.stream.Collectors;

public class FeatureRepresentorManager implements Initialisable
{
    private static final int[] MAX_TOP_TERMS = {2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192};

    private final Map<Metadata, FeatureRepresentor<StockQuote>> quoteRepresentors;
    private final Map<Metadata, FeatureRepresentor<CleanedDocument>> documentRepresentors;

    private final List<MissingDataPolicy> missingDataPolicies;

    public FeatureRepresentorManager()
    {
        quoteRepresentors = new LinkedHashMap<>();
        documentRepresentors = new LinkedHashMap<>();
        missingDataPolicies = new ArrayList<>();
    }

    @Override
    public void initialise()
    {
        // Stock Quotes
        quoteRepresentors.put(new Metadata("Quote (With Volume)","Stock Quote Data with Volume Traded Present",
                                           StockQuoteDataType.values().length),
                              new StockQuoteFeatureRepresentor(StockQuoteDataType.values()));
        quoteRepresentors.put(new Metadata("Quote (No Volume)", "Stock Quote Data with Volume Traded Excluded",
                        StockQuoteDataType.values().length - 1),
                              new StockQuoteFeatureRepresentor(quoteTypesWithoutVolume()));

        // Document: Sentiment
        documentRepresentors.put(new Metadata("Binary Sentiment", "1 for mode Sentiment, 0 for all others",
                                              Sentiment.values().length),
                                 new BinarySentimentFeatureRepresentor());
        documentRepresentors.put(new Metadata("Normalised Sentiment",
                                   "In the range of [0,1] depending how positive (most = 1, "
                                                    + "least = 0)", 1),
                                 new NormalisedSentimentFeatureRepresentor());

        // Document: Top Term (TF_IDF)
        for (int maxTopTerms : MAX_TOP_TERMS)
        {
            documentRepresentors.put(new Metadata("Top Term (TFIDF, " + maxTopTerms + ")",
                                       "Relevancy Metric: Term Frequency Inverse Document Frequency, "
                                                        + "Max Top Terms: " + maxTopTerms,
                                                  maxTopTerms),
                                     new TopTermFeatureRepresentor(new TF_IDF(), maxTopTerms));
        }

        // Missing Data Policies
        missingDataPolicies.add(new IgnorePolicy());
    }

    private StockQuoteDataType[] quoteTypesWithoutVolume()
    {
        List<StockQuoteDataType> types = Arrays.stream(StockQuoteDataType.values())
                                               .filter(q -> q.equals(StockQuoteDataType.VOLUME))
                                               .collect(Collectors.toList());
        return types.toArray(new StockQuoteDataType[StockQuoteDataType.values().length - 1]);
    }

    public Map<Metadata, FeatureRepresentor<StockQuote>> getQuoteRepresentors()
    {
        return quoteRepresentors;
    }

    public Map<Metadata, FeatureRepresentor<CleanedDocument>> getDocumentRepresentors()
    {
        return documentRepresentors;
    }

    public List<MissingDataPolicy> getMissingDataPolicies()
    {
        return missingDataPolicies;
    }

    public class Metadata
    {
        private String name, description;
        /**
         * This should be an upper bound on the value.
         */
        private int estimatedVectorWidth;

        public Metadata(String name, String description, int estimatedVectorWidth)
        {
            this.name = name;
            this.description = description;
            this.estimatedVectorWidth = estimatedVectorWidth;
        }

        @Override
        public int hashCode()
        {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object obj)
        {
            return obj instanceof Metadata
                    && ((Metadata) obj).name.equalsIgnoreCase(name);
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getDescription()
        {
            return description;
        }

        public void setDescription(String description)
        {
            this.description = description;
        }

        public int getEstimatedVectorWidth()
        {
            return estimatedVectorWidth;
        }

        public void setEstimatedVectorWidth(int estimatedVectorWidth)
        {
            this.estimatedVectorWidth = estimatedVectorWidth;
        }
    }

}
