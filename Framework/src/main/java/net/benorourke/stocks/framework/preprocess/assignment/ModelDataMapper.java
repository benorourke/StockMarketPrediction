package net.benorourke.stocks.framework.preprocess.assignment;

import net.benorourke.stocks.framework.model.ModelData;
import net.benorourke.stocks.framework.preprocess.FeatureRepresentor;
import net.benorourke.stocks.framework.series.data.impl.*;

import java.util.Date;
import java.util.List;

public abstract class ModelDataMapper
{
    private final List<FeatureRepresentor<CleanedDocument>> documentRepresentors;
    private final List<FeatureRepresentor<StockQuote>> quoteRepresentors;
    private final StockQuoteDataType[] labelsToPredict;

    /**
     * @param quoteRepresentors the feature representors for extracting vectors from the stock quotes
     */
    public ModelDataMapper(List<FeatureRepresentor<CleanedDocument>> documentRepresentors,
                           List<FeatureRepresentor<StockQuote>> quoteRepresentors,
                           StockQuoteDataType[] labelsToPredict)
    {
        this.documentRepresentors = documentRepresentors;
        this.quoteRepresentors = quoteRepresentors;
        this.labelsToPredict = labelsToPredict;
    }

    /**
     *
     * @param date
     * @param documents
     * @param quotes should only be 1 StockQuote but we'll treat it as a list in case there's multiple sources
     * @return
     */
    public abstract ModelData toModelData(Date date, List<ProcessedDocument> documents, List<StockQuote> quotes);

    public List<FeatureRepresentor<CleanedDocument>> getDocumentRepresentors()
    {
        return documentRepresentors;
    }

    public List<FeatureRepresentor<StockQuote>> getQuoteRepresentors()
    {
        return quoteRepresentors;
    }

    public int getFeatureCount()
    {
        int sum = 0;
        for (FeatureRepresentor documentRepresentor : documentRepresentors)
            sum += documentRepresentor.getVectorSize();
        for (FeatureRepresentor quoteRepresentor : quoteRepresentors)
            sum += quoteRepresentor.getVectorSize();
        return sum;
    }

    public StockQuoteDataType[] getLabelsToPredict()
    {
        return labelsToPredict;
    }

    public int getLabelCount()
    {
        return labelsToPredict.length;
    }

}
