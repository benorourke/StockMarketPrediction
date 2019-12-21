package net.ben.stocks.framework.stock;

public class Stock
{
    private final StockExchange exchange;
    private final String companyName;
    private final String ticker;

    public Stock(StockExchange exchange, String companyName, String ticker)
    {
        this.exchange = exchange;
        this.companyName = companyName;
        this.ticker = ticker;
    }

    public StockExchange getExchange()
    {
        return exchange;
    }

    public String getCompanyName()
    {
        return companyName;
    }

    public String getTicker()
    {
        return ticker;
    }
}
