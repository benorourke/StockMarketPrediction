package net.benorourke.stocks.framework.preprocessor.impl;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.preprocessor.Preprocessor;
import net.benorourke.stocks.framework.series.data.impl.ProcessedStockQuote;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;

public class StockQuotePreprocessor implements Preprocessor<StockQuote, ProcessedStockQuote>
{

    @Override
    public void initialise() {}

    @Override
    public ProcessedStockQuote preprocess(StockQuote data)
    {
        // TODO - Normalise it here?
        return new ProcessedStockQuote(data.getDate(), data.getOpen(), data.getClose(),
                                       data.getHigh(), data.getLow());
    }

}
