package net.benorourke.stocks.framework.thread.preprocessing;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.exception.InsuficcientRawDataException;
import net.benorourke.stocks.framework.model.ModelData;
import net.benorourke.stocks.framework.persistence.store.DataStore;
import net.benorourke.stocks.framework.preprocess.FeatureRepresenter;
import net.benorourke.stocks.framework.preprocess.Preprocess;
import net.benorourke.stocks.framework.preprocess.ProgressCallback;
import net.benorourke.stocks.framework.preprocess.assignment.LabelAssignment;
import net.benorourke.stocks.framework.preprocess.assignment.MissingDataPolicy;
import net.benorourke.stocks.framework.preprocess.assignment.ModelDataMapper;
import net.benorourke.stocks.framework.preprocess.document.DimensionalityReduction;
import net.benorourke.stocks.framework.preprocess.document.FeatureRepresentation;
import net.benorourke.stocks.framework.model.ProcessedDataset;
import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.impl.*;
import net.benorourke.stocks.framework.thread.Task;
import net.benorourke.stocks.framework.thread.TaskDescription;
import net.benorourke.stocks.framework.thread.TaskType;
import net.benorourke.stocks.framework.util.Nullable;
import net.benorourke.stocks.framework.util.Tuple;

import java.util.*;

/**
 * No TaskDescription derivative class required as we only want to enable one
 * pre-processing task at a time.
 */
public class PreprocessingTask implements Task<TaskDescription, PreprocessingResult>
{
    private final DataStore store;
    private final Map<DataSource, Integer> collectedDataCounts;
    private final List<FeatureRepresenter<CleanedDocument>> documentFeatureRepresenters;
    private final List<FeatureRepresenter<StockQuote>> quoteFeatureRepresenters;


    // Document Pre-processes
    private final DimensionalityReduction dimensionalityReduction;
    private final FeatureRepresentation featureRepresentation;
    // Document & StockQuote Preprocesses
    private final LabelAssignment labelAssignment;
    // All Preprocesses
    private final Preprocess[] preprocesses;

    private PreprocessingStage stage;
    private DataSource<StockQuote> stockQuoteSource;
    private List<DataSource<Document>> documentSources;

    private PreprocessingProgress progress;

    // Data that's loaded/processed progressively:
    private List<StockQuote> loadedQuotes;
    @Nullable
    private List<Document> loadedCorpus;
    @Nullable
    private List<CleanedDocument> reducedCorpus;
    @Nullable
    private List<ProcessedDocument> representedCorpus;
    @Nullable
    private ProcessedDataset result;

    /**
     * Any FeatureRepresenter, for either CleanedDocuments or StockQuotes should have an associated, registered GSON
     * TypeAdapter to ensure their persistence.
     *
     * @param store
     * @param collectedDataCounts
     * @param documentFeatureRepresenters
     * @param quoteFeatureRepresenters
     * @param missingDataPolicy
     * @param modelDataMapper
     * @throws InsuficcientRawDataException
     */
    public PreprocessingTask(DataStore store, Map<DataSource, Integer> collectedDataCounts,
                             List<FeatureRepresenter<CleanedDocument>> documentFeatureRepresenters,
                             List<FeatureRepresenter<StockQuote>> quoteFeatureRepresenters,
                             MissingDataPolicy missingDataPolicy,
                             ModelDataMapper modelDataMapper)
            throws InsuficcientRawDataException
    {
        this.store = store;
        this.collectedDataCounts = collectedDataCounts;
        this.documentFeatureRepresenters = documentFeatureRepresenters;
        this.quoteFeatureRepresenters = quoteFeatureRepresenters;

        loadedQuotes = new ArrayList<>();
        loadedCorpus = new ArrayList<>();
        reducedCorpus = new ArrayList<>();

        // Processes
        dimensionalityReduction = new DimensionalityReduction();
        featureRepresentation = new FeatureRepresentation(documentFeatureRepresenters);
        labelAssignment = new LabelAssignment(missingDataPolicy, modelDataMapper);
        preprocesses = new Preprocess[] {dimensionalityReduction, featureRepresentation, labelAssignment};

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
            case LOAD_CORPUS:
                executeLoadCorpus();
                return true;

            case DIMENSIONALITY_REDUCTION:
                executeReduceDimensionality();
                return true;
            case FEATURE_REPRESENTATION:
                executeRepresentFeatures();
                return true;
            case LABEL_ASSIGNMENT:
                executeAssignLabels();
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
        loadedQuotes.addAll(store.loadRawStockQuotes(stockQuoteSource));
        Framework.info("Loaded " + loadedQuotes.size() + " quotes to pre-process");
    }

    private void executeLoadCorpus()
    {
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

            loadedCorpus.addAll(store.loadRawDocuments(source));
            int loaded = loadedCorpus.size() - documentsLoaded;

            currentSource ++;
            documentsLoaded = loadedCorpus.size();
            double percentage = ((double) documentsLoaded / (double) totalDocuments) * 100;
            Framework.info("Loaded from DocumentSource " + source.getClass().getSimpleName()
                                + " (" + loaded +" loaded, " + percentage + "% complete)");
        }
    }

    private void executeReduceDimensionality()
    {
        reducedCorpus.addAll(dimensionalityReduction.preprocess(loadedCorpus));
        Framework.info("[Pre-processing] Reduced Dimensionality of Entire Corpus ("
                            + reducedCorpus.size() + ")");
        loadedCorpus.clear();
        loadedCorpus = null;
    }

    private void executeRepresentFeatures()
    {
        representedCorpus = featureRepresentation.preprocess(reducedCorpus);
        Framework.info("[Pre-processing] Represented Entire Corpus");
        reducedCorpus.clear();
        reducedCorpus = null;
    }

    private void executeAssignLabels()
    {
        Framework.info("[Pre-processing] Assigning Labels");
        int features = labelAssignment.getMapper().getFeatureCount();
        int labels = labelAssignment.getMapper().getLabelCount();
        List<ModelData> data = labelAssignment.preprocess(new Tuple<>(representedCorpus, loadedQuotes));
        Framework.info("[Pre-processing] Producing Corpus");
        result = new ProcessedDataset(documentFeatureRepresenters, quoteFeatureRepresenters, features, labels, data);
        Framework.info("[Pre-processing] Normalising Corpus");
        result.calculateFeatureMinsMaxes();
        result.normalise();
        Framework.info("[Pre-processing] Complete");
    }

    @Override
    public boolean isFinished()
    {
        return stage.equals(PreprocessingStage.DONE);
    }

    @Override
    public PreprocessingResult getResult()
    {
        return new PreprocessingResult(result);
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
