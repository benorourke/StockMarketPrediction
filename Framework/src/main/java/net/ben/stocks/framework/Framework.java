package net.ben.stocks.framework;

import net.ben.stocks.framework.collection.DataSourceManager;
import net.ben.stocks.framework.series.TimeSeriesManager;
import net.ben.stocks.framework.stock.StockExchangeManager;
import net.ben.stocks.framework.util.Initialisable;

import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class Framework implements Initialisable
{
    private final StockExchangeManager stockExchangeManager;
    private final DataSourceManager dataSourceManager;
    private final TimeSeriesManager timeSeriesManager;

    public Framework()
    {
        File defaultDir = FileSystemView.getFileSystemView().getDefaultDirectory();
        File dir = new File(defaultDir + "/Stocks/");
        log("Using Directory " + dir.getPath());

        stockExchangeManager = new StockExchangeManager();
        dataSourceManager = new DataSourceManager();
        timeSeriesManager = new TimeSeriesManager(dir); // Takes about 100ms
    }

    @Override
    public void initialise()
    {
        stockExchangeManager.initialise();
        timeSeriesManager.initialise();
    }

    public void log(String message)
    {
        // TODO - Using log4j or other means of standardised logging
        System.out.println(message);
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

}
