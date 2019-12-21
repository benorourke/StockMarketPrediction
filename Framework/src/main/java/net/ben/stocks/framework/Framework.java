package net.ben.stocks.framework;

import net.ben.stocks.framework.collection.DataSourceManager;
import net.ben.stocks.framework.stock.StockExchangeManager;
import net.ben.stocks.framework.util.Initialisable;

public class Framework implements Initialisable
{
    private final StockExchangeManager stockExchangeManager;
    private final DataSourceManager dataSourceManager;

    public Framework()
    {
        stockExchangeManager = new StockExchangeManager();
        dataSourceManager = new DataSourceManager();
    }

    @Override
    public void initialise()
    {
        stockExchangeManager.initialise();
    }

    public StockExchangeManager getStockExchangeManager()
    {
        return stockExchangeManager;
    }

    public DataSourceManager getDataSourceManager()
    {
        return dataSourceManager;
    }

}
