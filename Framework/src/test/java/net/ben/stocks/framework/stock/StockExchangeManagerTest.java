package net.ben.stocks.framework.stock;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StockExchangeManagerTest
{

    @Test
    public void equalsMethod_DifferingStockExchangeInstances_ShouldReturnTrue() {
        StockExchange exchange1 = new StockExchange("New York Stock Exchange", "NYSE");
        StockExchange exchange2 = new StockExchange("New York Stock Exchange", "NYSE");

        assertEquals(true, exchange1.equals(exchange2));
    }

    @Test
    public void equalsMethod_DifferingStockInstances_ShouldReturnTrue() {
        StockExchange exchange = new StockExchange("New York Stock Exchange", "NYSE");
        Stock stock1 = new Stock(exchange, "Amazon", "AMZN");
        Stock stock2 = new Stock(exchange, "Amazon", "AMZN");

        assertEquals(true, stock1.equals(stock2));
    }


}
