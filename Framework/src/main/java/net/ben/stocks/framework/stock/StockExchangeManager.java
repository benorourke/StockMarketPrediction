package net.ben.stocks.framework.stock;

import net.ben.stocks.framework.util.Initialisable;

import java.util.ArrayList;
import java.util.List;

public class StockExchangeManager implements Initialisable
{
    private final List<StockExchange> exchanges;

    public StockExchangeManager()
    {
        exchanges = new ArrayList<StockExchange>();
    }

    @Override
    public void initialise()
    {
        StockExchange nyse = new StockExchange("New York Stock Exchange", "NYSE");
        nyse.getStocks().add(new Stock(nyse, "Apple", "AAPL"));

        exchanges.add(nyse);
    }

    public StockExchange getByName(String exchangeName)
    {
        return exchanges.stream()
                    .filter(e -> e.getName().equalsIgnoreCase(exchangeName))
                    .findFirst()
                    .orElse(null);
    }

    public List<StockExchange> getExchanges()
    {
        return exchanges;
    }

}
