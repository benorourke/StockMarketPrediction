package net.benorourke.stocks.framework.preprocess.combination;

import net.benorourke.stocks.framework.model.ModelData;
import net.benorourke.stocks.framework.series.data.impl.ProcessedDocument;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;

import java.util.Date;
import java.util.List;

public interface ModelDataMapper
{

    int getFeatureCount(List<ProcessedDocument> documents, List<StockQuote> quotes);

    int getLabelCount(List<ProcessedDocument> documents, List<StockQuote> quotes);

    /**
     *
     * @param date
     * @param documents
     * @param quotes should only be 1 StockQuote but we'll treat it as a list in case there's multiple sources
     * @return
     */
    ModelData toModelData(Date date, List<ProcessedDocument> documents, List<StockQuote> quotes);

}
