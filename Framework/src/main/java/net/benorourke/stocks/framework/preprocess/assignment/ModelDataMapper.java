package net.benorourke.stocks.framework.preprocess.assignment;

import net.benorourke.stocks.framework.model.ModelData;
import net.benorourke.stocks.framework.preprocess.FeatureRepresenter;
import net.benorourke.stocks.framework.series.data.impl.*;

import java.util.Date;
import java.util.List;

public abstract class ModelDataMapper
{
    private final List<FeatureRepresenter<CleanedDocument>> documentRepresenters;
    private final List<FeatureRepresenter<StockQuote>> quoteRepresenters;
    private final StockQuoteDataType[] labelsToPredict;

    /**
     * @param quoteRepresenters the feature representers for extracting vectors from the stock quotes
     */
    public ModelDataMapper(List<FeatureRepresenter<CleanedDocument>> documentRepresenters,
                           List<FeatureRepresenter<StockQuote>> quoteRepresenters,
                           StockQuoteDataType[] labelsToPredict)
    {
        this.documentRepresenters = documentRepresenters;
        this.quoteRepresenters = quoteRepresenters;
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

    public List<FeatureRepresenter<CleanedDocument>> getDocumentRepresenters()
    {
        return documentRepresenters;
    }

    public List<FeatureRepresenter<StockQuote>> getQuoteRepresenters()
    {
        return quoteRepresenters;
    }

    public int getFeatureCount()
    {
        int sum = 0;
        for (FeatureRepresenter documentRepresenter : documentRepresenters)
            sum += documentRepresenter.getVectorSize();
        for (FeatureRepresenter quoteRepresenter : quoteRepresenters)
            sum += quoteRepresenter.getVectorSize();
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
