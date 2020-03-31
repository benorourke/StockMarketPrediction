package net.benorourke.stocks.framework;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.benorourke.stocks.framework.collection.datasource.DataSourceManager;
import net.benorourke.stocks.framework.model.ModelData;
import net.benorourke.stocks.framework.model.ModelEvaluation;
import net.benorourke.stocks.framework.model.ModelHandlerManager;
import net.benorourke.stocks.framework.model.ProcessedCorpus;
import net.benorourke.stocks.framework.persistence.FileManager;
import net.benorourke.stocks.framework.persistence.gson.StockAdapter;
import net.benorourke.stocks.framework.persistence.gson.TimeSeriesAdapter;
import net.benorourke.stocks.framework.persistence.gson.data.DocumentAdapter;
import net.benorourke.stocks.framework.persistence.gson.data.StockQuoteAdapter;
import net.benorourke.stocks.framework.persistence.gson.model.ModelDataAdapter;
import net.benorourke.stocks.framework.persistence.gson.model.ModelEvaluationAdapter;
import net.benorourke.stocks.framework.persistence.gson.model.ProcessedCorpusAdapter;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.series.TimeSeriesManager;
import net.benorourke.stocks.framework.series.data.impl.Document;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;
import net.benorourke.stocks.framework.stock.Stock;
import net.benorourke.stocks.framework.stock.StockExchangeManager;
import net.benorourke.stocks.framework.thread.TaskManager;
import net.benorourke.stocks.framework.util.Initialisable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Framework should only be accessed via a single thread.
 */
public class Framework implements Initialisable
{
    private static final Logger logger;

    private final FileManager fileManager;
    private final StockExchangeManager stockExchangeManager;
    private final DataSourceManager dataSourceManager;
    private final TimeSeriesManager timeSeriesManager;
    private final TaskManager taskManager;
    private final ModelHandlerManager modelHandlerManager;

    private final Gson gson;

    static
    {
        logger = LoggerFactory.getLogger(Framework.class);
    }

    public Framework(Configuration config)
    {
        fileManager = new FileManager(this, config);
        stockExchangeManager = new StockExchangeManager();
        dataSourceManager = new DataSourceManager();
        timeSeriesManager = new TimeSeriesManager(this);
        taskManager = new TaskManager(config);
        modelHandlerManager = new ModelHandlerManager();

        gson = new GsonBuilder()
                        .setPrettyPrinting()
                        .registerTypeAdapter(Stock.class, new StockAdapter(stockExchangeManager))
                        .registerTypeAdapter(TimeSeries.class, new TimeSeriesAdapter())
                        .registerTypeAdapter(StockQuote.class, new StockQuoteAdapter())
                        .registerTypeAdapter(Document.class, new DocumentAdapter())
                        .registerTypeAdapter(ModelData.class, new ModelDataAdapter())
                        .registerTypeAdapter(ProcessedCorpus.class, new ProcessedCorpusAdapter())
                        .registerTypeAdapter(ModelEvaluation.class, new ModelEvaluationAdapter())
                        .create();
    }

    public Framework()
    {
        this(new Configuration());
    }

    @Override
    public void initialise()
    {
        stockExchangeManager.initialise();
        timeSeriesManager.initialise();
        modelHandlerManager.initialise();
    }

    public static void info(String message)
    {
        logger.info(message);
    }

    public static void debug(String message)
    {
        logger.info("Debug: " + message);
    }

    public static void error(String message)
    {
        logger.error(message);
    }

    public static void error(String message, Throwable throwable)
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

    public TaskManager getTaskManager()
    {
        return taskManager;
    }

    public ModelHandlerManager getModelHandlerManager()
    {
        return modelHandlerManager;
    }

    /**
     * This should only be accessed through the FileManager
     * @return
     */
    public Gson getGson()
    {
        // TODO - Ensure this is thread-safe, it may be used concurrently
        return gson;
    }

}
