package net.benorourke.stocks.framework.preprocess.impl;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.preprocess.Preprocess;
import net.benorourke.stocks.framework.series.data.impl.ProcessedStockQuote;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;

import java.util.ArrayList;
import java.util.List;

public class StockQuoteProcess extends Preprocess<List<StockQuote>, List<ProcessedStockQuote>>
{
    private static final int PROGRESS_ITERATIONS = 50;

    @Override
    public void initialise() {}

    @Override
    public List<ProcessedStockQuote> preprocess(List<StockQuote> data)
    {
        List<ProcessedStockQuote> res = new ArrayList<ProcessedStockQuote>();
        int total = data.size(), count = 0;
        for (StockQuote quote : data)
        {
            // TODO - Normalisation?
            ProcessedStockQuote processed =
                    new ProcessedStockQuote(quote.getDate(), quote.getOpen(), quote.getClose(),
                                            quote.getHigh(), quote.getLow());
            res.add(processed);

            // Update Progress
            count ++;
            Framework.info("Preprocessed count: " + count);
            if(count % PROGRESS_ITERATIONS == 0)
                onProgressChanged(( (double) count / (double) total) * 100 );
        }

        onProgressChanged(100.0D);
        return res;
    }

}
