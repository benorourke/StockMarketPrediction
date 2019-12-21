package net.ben.stocks.framework.stock;

import net.ben.stocks.framework.util.Initialisable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StockExchangeManager implements Initialisable
{
    private List<StockExchange> exchanges;

    public StockExchangeManager()
    {
        exchanges = new ArrayList<StockExchange>();
    }

    @Override
    public void initialise()
    {
        StockExchange nyse = new StockExchange("New York Stock Exchange", "NYSE");
        nyse.getStocks().add(new Stock(nyse, "Amazon", "AMZN"));

        exchanges.add(nyse);
    }

    public List<StockExchange> getExchanges()
    {
        return exchanges;
    }

}
