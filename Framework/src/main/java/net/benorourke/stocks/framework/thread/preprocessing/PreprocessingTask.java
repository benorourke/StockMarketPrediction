package net.benorourke.stocks.framework.thread.preprocessing;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.exception.InsuficcientRawDataException;
import net.benorourke.stocks.framework.persistence.store.DataStore;
import net.benorourke.stocks.framework.preprocess.Preprocess;
import net.benorourke.stocks.framework.preprocess.ProgressCallback;
import net.benorourke.stocks.framework.preprocess.impl.StockQuoteNormaliser;
import net.benorourke.stocks.framework.preprocess.impl.document.DocumentCleaner;
import net.benorourke.stocks.framework.preprocess.impl.document.CorpusProcessor;
import net.benorourke.stocks.framework.model.ProcessedCorpus;
import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.impl.*;
import net.benorourke.stocks.framework.thread.Task;
import net.benorourke.stocks.framework.thread.TaskDescription;
import net.benorourke.stocks.framework.thread.TaskType;
import net.benorourke.stocks.framework.util.DateUtil;
import net.benorourke.stocks.framework.util.Nullable;

import static net.benorourke.stocks.framework.util.DateUtil.getDayStart;

import java.util.*;

/**
 * No TaskDescription derivative class required as we only want to enable one
 * pre-processing task at a time.
 */
public class PreprocessingTask implements Task<TaskDescription, PreprocessingResult>
{
    private final DataStore store;
    private final Map<DataSource, Integer> collectedDataCounts;

    private final Preprocess<List<StockQuote>, List<NormalisedStockQuote>> stockQuoteProcessor;
    private final Preprocess<List<Document>, List<CleanedDocument>> corpusCleaner;
    private final Preprocess<Map<Date, List<CleanedDocument>>, ProcessedCorpus> corpusProcessor;
    private final Preprocess[] preprocesses;

    private PreprocessingStage stage;
    private DataSource<StockQuote> stockQuoteSource;
    private List<DataSource<Document>> documentSources;

    private PreprocessingProgress progress;

    // ModelData that's loaded/processed progressively:
    @Nullable
    private List<StockQuote> loadedQuotes;
    private Map<Date, NormalisedStockQuote> normalisedQuotes;
    @Nullable
    private List<Document> loadedCorpus;
    @Nullable
    private Map<Date, List<CleanedDocument>> cleanDocuments;
    private ProcessedCorpus processedCorpus;

    public PreprocessingTask(DataStore store,
                             Map<DataSource, Integer> collectedDataCounts)
            throws InsuficcientRawDataException
    {
        this.store = store;
        this.collectedDataCounts = collectedDataCounts;

        normalisedQuotes = new HashMap<>();
        cleanDocuments = new HashMap<>();

        // Processes
        stockQuoteProcessor = new StockQuoteNormaliser();
        corpusCleaner = new DocumentCleaner();
        corpusProcessor = new CorpusProcessor(normalisedQuotes);
        preprocesses = new Preprocess[]{stockQuoteProcessor, corpusCleaner, corpusProcessor};

        stage = PreprocessingStage.first();

        Map<DataType, List<DataSource>> grouped = group(collectedDataCounts.keySet());
        // Check there is sufficient feedforward:
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
    public PreprocessingProgress createTaskProgress()
    {
        return progress = new PreprocessingProgress(preprocesses);
    }

    @Override
    public void run()
    {
        Framework.info("Executing stage " + stage.toString());
        if (executeStage())
        {
            progress.onStageCompleted(stage);
            stage = stage.next();
        }
    }

    /**
     * @return whether to move on to the next stage
     */
    public boolean executeStage()
    {
        switch (stage)
        {
            case INITIALISE_PREPROCESSES:
                initialisePreprocesses();
                return true;
            case LOAD_QUOTES:
                executeLoadQuotes();
                return true;
            case NORMALISE_QUOTES:
                executeNormaliseQuotes();
                return true;
            case LOAD_CORPUS:
                executeLoadCorpus();
                return true;
            case CLEAN_CORPUS:
                executeCleanCorpus();
                return true;
            case PROCESS_CORPUS:
                executeProcessCorpus();
                return true;
            case DONE:
                break;
        }
        return false;
    }

    private void initialisePreprocesses()
    {
        for (int i = 0; i < preprocesses.length; i ++)
        {
            final int finalI = i;
            Preprocess preprocess = preprocesses[i];
            preprocess.initialise();
            preprocess.addProgressCallback(new ProgressCallback()
            {
                @Override
                public void onProgressUpdate(double percentageProgress)
                {
                    progress.onPreprocessorPercentageChanged(finalI, percentageProgress);
                }
            });
        }

        Framework.info("Initialised " + preprocesses.length + " preprocesses");
    }

    private void executeLoadQuotes()
    {
        Class<? extends DataSource<StockQuote>> stockQuoteSourceClazz
                = (Class<? extends DataSource<StockQuote>>) stockQuoteSource.getClass();
        loadedQuotes = store.loadRawStockQuotes(stockQuoteSourceClazz);
        Framework.info("Loaded " + loadedQuotes.size() + " quotes to pre-process");
    }

    private void executeNormaliseQuotes()
    {
        Framework.info("Using Preprocess " + stockQuoteProcessor.getClass().getSimpleName()
                                + " to normalise " + loadedQuotes.size() + " quotes");

        for (NormalisedStockQuote processed : stockQuoteProcessor.preprocess(loadedQuotes))
        {
            Date date = getDayStart(processed.getDate());
            normalisedQuotes.put(date, processed);
        }

        Framework.info("Normalised " + loadedQuotes.size() + " quotes. Dumping unprocessed quotes.");
        loadedQuotes.clear();
        loadedQuotes = null;
    }

    private void executeLoadCorpus()
    {
        loadedCorpus = new ArrayList<>();

        int totalSources = documentSources.size(), currentSource = 1;
        int totalDocuments = documentSources.stream()
                                    .mapToInt(s -> (s.getDataType().equals(DataType.DOCUMENT))
                                                        ? collectedDataCounts.get(s)
                                                        : 0)
                                    .sum();

        Framework.info("Expecting " + totalDocuments + " Documents to be loaded");

        int documentsLoaded = 0;
        for (DataSource<Document> source : documentSources)
        {
            Framework.info("Loading from DocumentSource " + source.getClass().getSimpleName()
                                + " (" + currentSource + '/' + totalSources + ")");

            Class<? extends DataSource<Document>> documentSourceClazz
                    = (Class<? extends DataSource<Document>>) source.getClass();
            loadedCorpus.addAll(store.loadRawDocuments(documentSourceClazz));
            int loaded = loadedCorpus.size() - documentsLoaded;

            currentSource ++;
            documentsLoaded = loadedCorpus.size();
            double percentage = ((double) documentsLoaded / (double) totalDocuments) * 100;
            Framework.info("Loaded from DocumentSource " + source.getClass().getSimpleName()
                                + " (" + loaded +" loaded, " + percentage + "% complete)");
        }
    }

    private void executeCleanCorpus()
    {
        // TODO: Make the next step it's own stage as inserting this many values could
        //       take a long time
        for (CleanedDocument processed : corpusCleaner.preprocess(loadedCorpus))
        {
            Date date = getDayStart(processed.getDate());

//            Framework.debug("Received processed document on " + DateUtil.formatDetailed(date));

            if (!cleanDocuments.containsKey(date))
            {
                Framework.debug("[Clean] Date added: " + DateUtil.formatDetailed(date)
                                    + " (" + DateUtil.formatDetailed(processed.getDate()) + ")");
                cleanDocuments.put(date, new ArrayList<>());
            }

            cleanDocuments.get(date).add(processed);
        }

        Framework.info("Cleaned " + loadedCorpus.size() + " documents across "
                            + cleanDocuments.size() + " days. Dumping uncleaned corpus.");
        loadedCorpus.clear();
        loadedCorpus = null;
    }

    private void executeProcessCorpus()
    {
        processedCorpus = corpusProcessor.preprocess(cleanDocuments);
        Framework.info("Processed entire corpus.");
        cleanDocuments.clear();
        cleanDocuments = null;
    }

    @Override
    public boolean isFinished()
    {
        return stage.equals(PreprocessingStage.DONE);
    }

    @Override
    public PreprocessingResult getResult()
    {
        return new PreprocessingResult(processedCorpus);
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
