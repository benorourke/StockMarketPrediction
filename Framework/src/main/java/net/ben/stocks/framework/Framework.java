package net.ben.stocks.framework;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.ben.stocks.framework.collection.DataSourceManager;
import net.ben.stocks.framework.persistence.gson.FileManager;
import net.ben.stocks.framework.persistence.gson.StockAdapter;
import net.ben.stocks.framework.series.TimeSeriesManager;
import net.ben.stocks.framework.stock.Stock;
import net.ben.stocks.framework.stock.StockExchangeManager;
import net.ben.stocks.framework.util.Initialisable;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class Framework implements Initialisable
{
    private static final Logger logger;

    private final FileManager fileManager;
    private final StockExchangeManager stockExchangeManager;
    private final DataSourceManager dataSourceManager;
    private final TimeSeriesManager timeSeriesManager;

    private final Gson gson;

    static
    {
        BasicConfigurator.configure();
        logger = Logger.getLogger(Framework.class);
    }

    public Framework()
    {
        fileManager = new FileManager(this);
        stockExchangeManager = new StockExchangeManager();
        dataSourceManager = new DataSourceManager();
        timeSeriesManager = new TimeSeriesManager(this);

        gson = new GsonBuilder()
                        .registerTypeAdapter(Stock.class, new StockAdapter(stockExchangeManager))
                        .create();
    }

    @Override
    public void initialise()
    {
        stockExchangeManager.initialise();
    }

    public void info(String message)
    {
        logger.info(message);
    }

    public void debug(String message)
    {
        logger.debug(message);
    }

    public void error(String message)
    {
        logger.error(message);
    }

    public void error(String message, Throwable throwable)
    {
        logger.error(message, throwable);
    }

    public FileManager getFileManager()
    {
        return fileManager;
    }

    public StockExchangeManager getStockExchangeManager()
    {
        return stockExchangeManager;
    }

    public DataSourceManager getDataSourceManager()
    {
        return dataSourceManager;
    }

    public TimeSeriesManager getTimeSeriesManager()
    {
        return timeSeriesManager;
    }

    public Gson getGson()
    {
        return gson;
    }

}
