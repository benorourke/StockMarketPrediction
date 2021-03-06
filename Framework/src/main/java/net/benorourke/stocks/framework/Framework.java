package net.benorourke.stocks.framework;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.benorourke.stocks.framework.collection.datasource.DataSourceManager;
import net.benorourke.stocks.framework.collection.datasource.variable.Validators;
import net.benorourke.stocks.framework.collection.datasource.variable.VariableValidator;
import net.benorourke.stocks.framework.model.ModelData;
import net.benorourke.stocks.framework.model.ModelEvaluation;
import net.benorourke.stocks.framework.model.ModelHandlerManager;
import net.benorourke.stocks.framework.model.ProcessedDataset;
import net.benorourke.stocks.framework.persistence.FileManager;
import net.benorourke.stocks.framework.persistence.gson.TimeSeriesAdapter;
import net.benorourke.stocks.framework.persistence.gson.data.DocumentAdapter;
import net.benorourke.stocks.framework.persistence.gson.data.StockQuoteAdapter;
import net.benorourke.stocks.framework.persistence.gson.data.representor.BinarySentimentFeatureRepresentorAdapter;
import net.benorourke.stocks.framework.persistence.gson.data.representor.NormalisedSentimentFeatureRepresentorAdapter;
import net.benorourke.stocks.framework.persistence.gson.data.representor.StockQuoteFeatureRepresentorAdapter;
import net.benorourke.stocks.framework.persistence.gson.data.representor.TopTermFeatureRepresentorAdapter;
import net.benorourke.stocks.framework.persistence.gson.model.ModelDataAdapter;
import net.benorourke.stocks.framework.persistence.gson.model.ModelEvaluationAdapter;
import net.benorourke.stocks.framework.persistence.gson.model.ProcessedDatasetAdapter;
import net.benorourke.stocks.framework.preprocess.FeatureRepresentorManager;
import net.benorourke.stocks.framework.preprocess.document.representor.sentiment.BinarySentimentFeatureRepresentor;
import net.benorourke.stocks.framework.preprocess.document.representor.sentiment.NormalisedSentimentFeatureRepresentor;
import net.benorourke.stocks.framework.preprocess.document.representor.topterm.TopTermFeatureRepresentor;
import net.benorourke.stocks.framework.preprocess.quote.StockQuoteFeatureRepresentor;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.series.TimeSeriesManager;
import net.benorourke.stocks.framework.series.data.impl.Document;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;
import net.benorourke.stocks.framework.thread.TaskManager;
import net.benorourke.stocks.framework.util.Initialisable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * The Framework should only be accessed via a single thread.
 */
public class Framework implements Initialisable
{
    private static final Logger logger;

    /** The framework-wide instance of gson used for serialization. */
    private final Gson gson;

    private final FileManager fileManager;
    private final DataSourceManager dataSourceManager;
    private final TimeSeriesManager timeSeriesManager;
    private final TaskManager taskManager;
    private final FeatureRepresentorManager featureRepresentorManager;
    private final ModelHandlerManager modelHandlerManager;

    static
    {
        logger = LoggerFactory.getLogger(Framework.class);
    }

    /**
     * Create a new instance with a specified configuration.
     *
     * @param config the configuration
     */
    public Framework(Configuration config)
    {
        GsonBuilder builder = new GsonBuilder()
                //                        .setPrettyPrinting()
                .registerTypeAdapter(TimeSeries.class, new TimeSeriesAdapter())
                .registerTypeAdapter(StockQuote.class, new StockQuoteAdapter())
                .registerTypeAdapter(Document.class, new DocumentAdapter())
                .registerTypeAdapter(ModelData.class, new ModelDataAdapter())
                .registerTypeAdapter(ProcessedDataset.class, new ProcessedDatasetAdapter())
                .registerTypeAdapter(ModelEvaluation.class, new ModelEvaluationAdapter())
                .registerTypeAdapter(TopTermFeatureRepresentor.class, new TopTermFeatureRepresentorAdapter())
                .registerTypeAdapter(BinarySentimentFeatureRepresentor.class, new BinarySentimentFeatureRepresentorAdapter())
                .registerTypeAdapter(NormalisedSentimentFeatureRepresentor.class, new NormalisedSentimentFeatureRepresentorAdapter())
                .registerTypeAdapter(StockQuoteFeatureRepresentor.class, new StockQuoteFeatureRepresentorAdapter());
        // Register the type adapters specified in the configuration
        config.getGsonTypeAdapters().entrySet()
                                        .stream()
                                        .forEach(e -> builder.registerTypeAdapter(e.getKey(), e.getValue()));
        gson = builder.create();

        // Inject any additional, custom VariableValidators for new DataSources.
        // This must happen before the DataSources are instantiated
        for (Map.Entry<String, VariableValidator> entry : config.getCollectionValidators().entrySet())
            Validators.inject(entry.getKey(), entry.getValue());

        fileManager = new FileManager(this, config);
        dataSourceManager = new DataSourceManager();
        timeSeriesManager = new TimeSeriesManager(this);
        taskManager = new TaskManager(config);
        featureRepresentorManager = new FeatureRepresentorManager();
        modelHandlerManager = new ModelHandlerManager();
    }

    /**
     * Create a new instance with the default configuration.
     */
    public Framework()
    {
        this(new Configuration());
    }

    @Override
    public void initialise()
    {
        timeSeriesManager.initialise();
        featureRepresentorManager.initialise();
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

    public FeatureRepresentorManager getFeatureRepresentorManager()
    {
        return featureRepresentorManager;
    }

    public ModelHandlerManager getModelHandlerManager()
    {
        return modelHandlerManager;
    }

    public Gson getGson()
    {
        return gson;
    }

}
