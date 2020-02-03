package net.ben.stocks.framework.series.data;

import java.util.Date;

public class StockQuote extends Data
{
    private double open, close;
    private double high, low;

    public StockQuote(Date date, double open, double close, double high, double low)
    {
        super(DataType.STOCK_QUOTE, date);

        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
    }

    @Override
    public boolean isProcessed()
    {
        return true;
    }

}
