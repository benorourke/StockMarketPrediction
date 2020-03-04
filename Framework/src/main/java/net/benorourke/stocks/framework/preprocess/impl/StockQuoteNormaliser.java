package net.benorourke.stocks.framework.preprocess.impl;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.preprocess.Preprocess;
import net.benorourke.stocks.framework.series.data.impl.NormalisedStockQuote;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;
import net.benorourke.stocks.framework.series.data.impl.StockQuoteDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StockQuoteNormaliser extends Preprocess<List<StockQuote>, List<NormalisedStockQuote>>
{
    private static final int CATEGORIES = StockQuoteDataType.count();
    private static final int PROGRESS_ITERATIONS = 50;

    @Override
    public void initialise() {}

    @Override
    public List<NormalisedStockQuote> preprocess(List<StockQuote> data)
    {
        // size * 2 because we crawl through data twice
        int total = data.size() * 2, count = 0;

        Framework.debug("Finding min/max for StockQuote data");
        double[] minimums = new double[CATEGORIES];
        Arrays.fill(minimums, Double.MAX_VALUE);
        double[] maximums = new double[CATEGORIES];
        Arrays.fill(maximums, Double.MIN_VALUE);
        for (StockQuote quote : data)
        {
            double[] quoteData = quote.getData();
            for (int i = 0; i < CATEGORIES; i ++)
            {
                if (minimums[i] > quoteData[i])
                    minimums[i] = quoteData[i];
                if (maximums[i] < quoteData[i])
                    maximums[i] = quoteData[i];
            }

            count ++;
            if(count % PROGRESS_ITERATIONS == 0)
                onProgressChanged(( (double) count / (double) total) * 100 );
        }
        Framework.debug("Found min/max for StockQuote data");

        Framework.debug("Min: " + Arrays.toString(minimums));
        Framework.debug("Max: " + Arrays.toString(maximums));

        List<NormalisedStockQuote> res = new ArrayList<NormalisedStockQuote>();
        for (StockQuote quote : data)
        {
            // Normalise the Quote values between [0, 1]
            double[] normalised = new double[CATEGORIES];
            for (int i = 0; i < CATEGORIES; i ++)
            {
                double unnormalisedValue = quote.getData()[i];
                normalised[i] = (unnormalisedValue - minimums[i]) / (maximums[i] - minimums[i]);
            }

            res.add(new NormalisedStockQuote(quote.getDate(), quote.getData(), normalised));

            Framework.debug(Arrays.toString(quote.getData()) + " -> " + Arrays.toString(normalised));

            // Update Progress
            count ++;
            if(count % PROGRESS_ITERATIONS == 0)
                onProgressChanged(( (double) count / (double) total) * 100 );
        }

        onProgressChanged(100.0D);
        return res;
    }

}
