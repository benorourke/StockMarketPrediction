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

import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class Framework implements Initialisable
{
    private final FileManager fileManager;
    private final StockExchangeManager stockExchangeManager;
    private final DataSourceManager dataSourceManager;
    private final TimeSeriesManager timeSeriesManager;

    private final Gson gson;

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

    public void log(String message)
    {
        // TODO - Using log4j or some other means of logging
        System.out.println(message);
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
