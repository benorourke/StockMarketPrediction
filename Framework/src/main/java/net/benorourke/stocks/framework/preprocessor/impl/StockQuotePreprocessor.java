package net.benorourke.stocks.framework.preprocessor.impl;

import net.benorourke.stocks.framework.preprocessor.Preprocessor;
import net.benorourke.stocks.framework.series.data.impl.ProcessedStockQuote;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;

import java.util.List;
import java.util.stream.Collectors;

public class StockQuotePreprocessor implements Preprocessor<StockQuote, ProcessedStockQuote>
{

    @Override
    public void initialise() {}

    @Override
    public List<ProcessedStockQuote> preprocess(List<StockQuote> data)
    {
        // TODO - Add some form of normalisation?
        return data.stream()
                .map(q -> new ProcessedStockQuote(q.getDate(), q.getOpen(), q.getClose(),
                                                  q.getHigh(), q.getLow()))
                .collect(Collectors.toList());
    }

}
