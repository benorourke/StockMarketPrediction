package net.benorourke.stocks.framework.thread.preprocessing;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.exception.InsuficcientRawDataException;
import net.benorourke.stocks.framework.model.ModelData;
import net.benorourke.stocks.framework.persistence.store.DataStore;
import net.benorourke.stocks.framework.preprocess.FeatureRepresenter;
import net.benorourke.stocks.framework.preprocess.Preprocess;
import net.benorourke.stocks.framework.preprocess.ProgressAdapter;
import net.benorourke.stocks.framework.preprocess.assignment.LabelAssignment;
import net.benorourke.stocks.framework.preprocess.assignment.MissingDataPolicy;
import net.benorourke.stocks.framework.preprocess.assignment.ModelDataMapper;
import net.benorourke.stocks.framework.preprocess.document.DimensionalityReduction;
import net.benorourke.stocks.framework.preprocess.document.FeatureRepresentation;
import net.benorourke.stocks.framework.model.ProcessedDataset;
import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.impl.*;
import net.benorourke.stocks.framework.thread.Progress;
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
    private static final LinkedHashMap<Integer, Double> PROGRESS_STEPS;
    private static final int PROGRESS_INIT = 0;
    private static final int PROGRESS_LOAD_QUOTES = 1;
    private static final int PROGRESS_LOAD_CORPUS = 2;
    private static final int PROGRESS_REDUCE_DIMENSIONALITY = 3;
    private static final int PROGRESS_REPRESENT_FEATURES = 4;
    private static final int PROGRESS_ASSIGN_LABELS = 5;

    static
    {
        PROGRESS_STEPS = new LinkedHashMap<>();
        PROGRESS_STEPS.put(PROGRESS_INIT, 5.0D);
        PROGRESS_STEPS.put(PROGRESS_LOAD_QUOTES, 7.5D);
        PROGRESS_STEPS.put(PROGRESS_LOAD_CORPUS, 7.5D);
        PROGRESS_STEPS.put(PROGRESS_REDUCE_DIMENSIONALITY, 25.0D);
        PROGRESS_STEPS.put(PROGRESS_REPRESENT_FEATURES, 35.0D);
        PROGRESS_STEPS.put(PROGRESS_ASSIGN_LABELS, 25.0D);
    }

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
    /**
     * [0] = dimensionality reduction
     * [1] = feature representation
     * [3] = label assignment
     *
     * If these change, ensure {@link #initialisePreprocesses()} is updated to reflect the mappings from
     * preprocess -> progress mapping ID.
     */
    private final Preprocess[] preprocesses;

    private PreprocessingStage stage;
    private DataSource<StockQuote> stockQuoteSource;
    private List<DataSource<Document>> documentSources;

    private Progress progress;
    private Progress.Helper progressHelper;

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
    public Progress createTaskProgress()
    {
        progress = new Progress();
        progressHelper = new Progress.Helper(progress, PROGRESS_STEPS);
        return progress;
    }

    @Override
    public void run()
    {
        Framework.info("Executing stage " + stage.toString());
        if (executeStage())
            stage = stage.next();
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
            preprocess.addProgressAdapter(percentageProgress ->
            {
                int progressId;
                switch (finalI)
                {
                    case 0:
                        progressId = PROGRESS_REDUCE_DIMENSIONALITY;
                        break;
                    case 1:
                        progressId = PROGRESS_REPRESENT_FEATURES;
                        break;
                    case 2:
                    default:
                        progressId = PROGRESS_ASSIGN_LABELS;
                        break;
                }

                progressHelper.updatePercentage(progressId, percentageProgress);
            });

            double percentage = (100.0 * (i + 1.0)) / ((double) preprocesses.length);
            progressHelper.updatePercentage(PROGRESS_INIT,  percentage);
        }

        Framework.info("Initialised " + preprocesses.length + " preprocesses");
    }

    private void executeLoadQuotes()
    {
        loadedQuotes.addAll(store.loadRawStockQuotes(stockQuoteSource));
        progressHelper.updatePercentage(PROGRESS_LOAD_QUOTES,  100.0);
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
            progressHelper.updatePercentage(PROGRESS_LOAD_CORPUS,  percentage);
            Framework.info("Loaded from DocumentSource " + source.getClass().getSimpleName()
                                + " (" + loaded +" loaded, " + percentage + "% complete)");
        }
    }

    private void executeReduceDimensionality()
    {
        reducedCorpus.addAll(dimensionalityReduction.preprocess(loadedCorpus));
        progressHelper.updatePercentage(PROGRESS_REDUCE_DIMENSIONALITY,  100.0); // should already happen from the adapter, but just in case
        Framework.info("[Pre-processing] Reduced Dimensionality of Entire Corpus ("
                            + reducedCorpus.size() + ")");
        loadedCorpus.clear();
        loadedCorpus = null;
    }

    private void executeRepresentFeatures()
    {
        representedCorpus = featureRepresentation.preprocess(reducedCorpus);
        progressHelper.updatePercentage(PROGRESS_REPRESENT_FEATURES,  100.0); // should already happen from the adapter, but just in case
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
        progressHelper.updatePercentage(PROGRESS_REPRESENT_FEATURES,  100.0); // should already happen from the adapter, but just in case
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
