package net.benorourke.stocks.framework.thread.preprocessing;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.exception.InsuficcientRawDataException;
import net.benorourke.stocks.framework.persistence.store.DataStore;
import net.benorourke.stocks.framework.preprocessor.Preprocess;
import net.benorourke.stocks.framework.preprocessor.Preprocessor;
import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.impl.Document;
import net.benorourke.stocks.framework.series.data.impl.ProcessedStockQuote;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;
import net.benorourke.stocks.framework.thread.Progress;
import net.benorourke.stocks.framework.thread.Task;
import net.benorourke.stocks.framework.thread.TaskDescription;
import net.benorourke.stocks.framework.thread.TaskType;
import net.benorourke.stocks.framework.util.Nullable;

import java.util.*;

/**
 * No TaskDescription derivative class required as we only want to enable one
 * pre-processing task at a time.
 */
public class PreprocessingTask implements Task<TaskDescription, PreprocessingResult>
{
    private final DataStore store;
    private final Map<Class<? extends Data>, Preprocessor> preprocessors;

    private PreprocessingStage stage;
    private DataSource<StockQuote> stockQuoteSource;
    private List<DataSource<Document>> documentSources;

    private Progress progress;

    // Data that's loaded/processed progressively:
    @Nullable
    private List<StockQuote> quotes;
    @Nullable
    private List<ProcessedStockQuote> processedQuotes;

    public PreprocessingTask(DataStore store,
                             Map<Class<? extends Data>, Preprocessor> preprocessors,
                             Map<DataSource, Integer> collectedDataCounts)
            throws InsuficcientRawDataException
    {
        this.store = store;
        this.preprocessors = preprocessors;

        stage = PreprocessingStage.first();

        Map<DataType, List<DataSource>> grouped = group(collectedDataCounts.keySet());
        // Check there is sufficient data:
        checkSufficiency(grouped);

        stockQuoteSource = (DataSource<StockQuote>) grouped.get(DataType.STOCK_QUOTE).get(0);
        documentSources = new ArrayList<>();
        for (DataSource source : grouped.get(DataType.DOCUMENT))
            documentSources.add( (DataSource<Document>) source);
    }

    private void checkSufficiency(Map<DataType, List<DataSource>> grouped)
            throws InsuficcientRawDataException
    {
        List<DataType> insufficient = new ArrayList<>();
        if (!grouped.containsKey(DataType.STOCK_QUOTE) || grouped.get(DataType.STOCK_QUOTE).size() < 1)
            insufficient.add(DataType.STOCK_QUOTE);
        if (!grouped.containsKey(DataType.DOCUMENT) || grouped.get(DataType.DOCUMENT).size() < 1)
            insufficient.add(DataType.DOCUMENT);

        if (insufficient.size() > 0)
            throw new InsuficcientRawDataException(insufficient);
    }

    @Override
    public TaskType getType()
    {
        return TaskType.PRE_PROCESSING;
    }

    @Override
    public TaskDescription getDescription()
    {
        return new TaskDescription(TaskType.PRE_PROCESSING)
        {
            @Override
            public boolean equals(Object object)
            {
                return object instanceof TaskDescription
                        && ((TaskDescription) object).getType().equals(getType());
            }
        };
    }

    @Override
    public Progress createTaskProgress()
    {
        return progress = new Progress();
    }

    @Override
    public void run()
    {
        Framework.debug("Executing stage " + stage.toString());
        if (executeStage())
            stage = stage.next();

        Framework.debug("Next stage: " + stage.toString());
    }

    /**
     * @return whether to move on to the next stage
     */
    public boolean executeStage()
    {
        switch (stage)
        {
            case INITIALISE_PREPROCESSORS:
                initialisePreprocessors();
                return true;
            case LOADING_QUOTES:
                executeLoadQuotes();
                return true;
            case PROCESSING_QUOTES:
                executeProcessQuotes();
                return true;

            case DONE:
                break;
        }
        return false;
    }

    private void initialisePreprocessors()
    {
        for (Preprocessor preprocessor : preprocessors.values())
            preprocessor.initialise();

        Framework.info("Initialised " + preprocessors.size() + " preprocessors");
    }

    private void executeLoadQuotes()
    {
        Class<? extends DataSource<StockQuote>> stockQuoteSourceClazz
                = (Class<? extends DataSource<StockQuote>>) stockQuoteSource.getClass();
        quotes = store.loadRawData(stockQuoteSourceClazz, StockQuote.class);
        Framework.info("Loaded " + quotes.size() + " quotes to pre-process");

        for (StockQuote quote : quotes)
        {
            Framework.debug("Quote " + quote.toString());
        }
    }

    private void executeProcessQuotes()
    {
        processedQuotes = new ArrayList<ProcessedStockQuote>();
        Preprocessor preprocessor = preprocessors.get(StockQuote.class);
        Framework.info("Using Preprocessor " + preprocessor.getClass().getSimpleName()
                                + " to process " + quotes.size() + " quotes");

        Framework.debug("Test 1: quotes null=" + (quotes == null));
        for (StockQuote quote : quotes)
        {
            Framework.debug("Test 2");
            processedQuotes.add((ProcessedStockQuote) preprocessor.preprocess(quote));
            Framework.debug("Test 3");
        }

        Framework.info("Processed " + quotes.size() + " quotes");
    }

    @Override
    public boolean isFinished()
    {
        return stage.equals(PreprocessingStage.DONE);
    }

    @Override
    public PreprocessingResult getResult()
    {
        // TODO
        return new PreprocessingResult();
    }

    private Map<DataType, List<DataSource>> group(Collection<DataSource> sources)
    {
        Map<DataType, List<DataSource>> grouped = new HashMap<>();

        for (DataSource source : sources)
        {
            DataType dataType = source.getDataType();

            if(!grouped.containsKey(dataType))
                grouped.put(dataType, new ArrayList<>());

            grouped.get(dataType).add(source);
        }

        return grouped;
    }

}
