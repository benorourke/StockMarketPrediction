package net.benorourke.stocks.userinterface.scene.dashboard;

import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.model.ModelEvaluation;
import net.benorourke.stocks.framework.model.ModelHandlerManager;
import net.benorourke.stocks.framework.preprocess.FeatureRepresenter;
import net.benorourke.stocks.framework.preprocess.FeatureRepresenterManager;
import net.benorourke.stocks.framework.preprocess.assignment.MissingDataPolicy;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.series.data.impl.CleanedDocument;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;
import net.benorourke.stocks.framework.util.Nullable;

import java.io.File;
import java.util.*;

import static net.benorourke.stocks.userinterface.StockApplication.runBgThread;
import static net.benorourke.stocks.userinterface.StockApplication.runUIThread;

public class DashboardModel
{
    private final DashboardController controller;

    // NAVBAR
    private List<TimeSeries> timeSeries;
    @Nullable private TimeSeries currentlySelectedTimeSeries;

    // COLLECTION
    private List<DataSource> dataSources;
    private DataSource currentlySelectedDataSource;

    // PRE-PROCESSING
    private Map<FeatureRepresenterManager.Metadata, FeatureRepresenter<StockQuote>> quoteFeatureRepresenters;
    private Map<FeatureRepresenterManager.Metadata, FeatureRepresenter<CleanedDocument>> documentFeatureRepresenters;
    private List<MissingDataPolicy> missingDataPolicies;

    @Nullable private MissingDataPolicy currentlySelectedMissingDataPolicy;

    // TRAINING
    private List<ModelHandlerManager.RuntimeCreator> modelHandlerCreators;

    // EVALUATION
    private List<String> trainedModels;
    private ModelEvaluation lastAcquiredEvaluation;

    protected DashboardModel(DashboardController controller)
    {
        this.controller = controller;

        // Navbar
        timeSeries = new ArrayList<>();

        // Collection
        dataSources = new ArrayList<>();

        // Pre-processing
        quoteFeatureRepresenters = new HashMap<>();
        documentFeatureRepresenters = new HashMap<>();
        missingDataPolicies = new ArrayList<>();

        // Training
        modelHandlerCreators = new ArrayList<>();

        // Evaluation
        trainedModels = new ArrayList<>();
    }

    //////////////////////////////////////////////////////////////////
    //      TIMESERIES UNSPECIFIC (APPLIES TO ALL)
    //////////////////////////////////////////////////////////////////

    public void acquireTimeSeries(Runnable onRetrieval)
    {
        runBgThread(framework ->
        {
            final List<TimeSeries> clone = Collections.unmodifiableList(
                                                framework.getTimeSeriesManager().getTimeSeries());

            runUIThread(() ->
            {
                timeSeries = clone;
                onRetrieval.run();
            });
        });
    }

    public void acquireDataSources(@Nullable Runnable onRetrieval)
    {
        runBgThread(framework ->
        {
            final List<DataSource> clone = Collections.unmodifiableList(
                                                            framework.getDataSourceManager().getDataSources());

            runUIThread(() ->
            {
                dataSources = clone;
                if (onRetrieval != null) onRetrieval.run();
            });
        });
    }

    public void acquireModelHandlers(Runnable onRetrieval)
    {
        runBgThread(framework ->
        {
            final List<ModelHandlerManager.RuntimeCreator> creators =
                    Collections.unmodifiableList(framework.getModelHandlerManager().getCreators());

            runUIThread(() ->
            {
                modelHandlerCreators = creators;
                onRetrieval.run();
            });
        });
    }

    public void acquireFeatureRepresentionData(Runnable onRetrieval)
    {
        runBgThread(framework ->
        {
            final Map<FeatureRepresenterManager.Metadata, FeatureRepresenter<StockQuote>> features =
                    Collections.unmodifiableMap(framework.getFeatureRepresenterManager().getQuoteRepresenters());
            final Map<FeatureRepresenterManager.Metadata, FeatureRepresenter<CleanedDocument>> documents =
                    Collections.unmodifiableMap(framework.getFeatureRepresenterManager().getDocumentRepresenters());
            final List<MissingDataPolicy> missingDataPolicies =
                    Collections.unmodifiableList(framework.getFeatureRepresenterManager().getMissingDataPolicies());

            runUIThread(() ->
            {
                this.quoteFeatureRepresenters = features;
                this.documentFeatureRepresenters = documents;
                this.missingDataPolicies = missingDataPolicies;
                onRetrieval.run();
            });
        });
    }

    //////////////////////////////////////////////////////////////////
    //      TIMESERIES SPECIFIC
    //////////////////////////////////////////////////////////////////

    public void acquireTrainedModels(TimeSeries seriesFor, Runnable onRetrieval)
    {
        runBgThread(framework ->
        {
            final List<String> trained = framework.getTimeSeriesManager().getTrainedModels(seriesFor);

            runUIThread(() ->
            {
                trainedModels = trained;
                onRetrieval.run();
            });
        });
    }

    public void acquireEvaluation(TimeSeries seriesFor, String modelName, Runnable onRetrieval)
    {
        runBgThread(framework ->
        {
            File evalFile = framework.getFileManager().getModelEvaluationFile(seriesFor, modelName);
            final ModelEvaluation eval = framework.getFileManager().loadJson(evalFile, ModelEvaluation.class).get();

            if (eval == null) return;

            runUIThread(() ->
            {
                this.lastAcquiredEvaluation = eval;
                onRetrieval.run();
            });
        });
    }

    public List<TimeSeries> getTimeSeries()
    {
        return timeSeries;
    }

    @Nullable
    public TimeSeries getCurrentlySelectedTimeSeries()
    {
        return currentlySelectedTimeSeries;
    }

    public void setCurrentlySelectedTimeSeries(TimeSeries currentlySelectedTimeSeries)
    {
        this.currentlySelectedTimeSeries = currentlySelectedTimeSeries;
    }

    public List<DataSource> getDataSources()
    {
        return dataSources;
    }

    public DataSource getCurrentlySelectedDataSource()
    {
        return currentlySelectedDataSource;
    }

    public void setCurrentlySelectedDataSource(DataSource currentlySelectedDataSource)
    {
        this.currentlySelectedDataSource = currentlySelectedDataSource;
    }

    public List<ModelHandlerManager.RuntimeCreator> getModelHandlerCreators()
    {
        return modelHandlerCreators;
    }

    public List<String> getTrainedModels()
    {
        return trainedModels;
    }

    public ModelEvaluation getLastAcquiredEvaluation()
    {
        return lastAcquiredEvaluation;
    }

    public Map<FeatureRepresenterManager.Metadata, FeatureRepresenter<StockQuote>> getQuoteFeatureRepresenters()
    {
        return quoteFeatureRepresenters;
    }

    public Map<FeatureRepresenterManager.Metadata, FeatureRepresenter<CleanedDocument>> getDocumentFeatureRepresenters()
    {
        return documentFeatureRepresenters;
    }

    public List<MissingDataPolicy> getMissingDataPolicies()
    {
        return missingDataPolicies;
    }

    public MissingDataPolicy getCurrentlySelectedMissingDataPolicy()
    {
        return currentlySelectedMissingDataPolicy;
    }

    public void setCurrentlySelectedMissingDataPolicy(MissingDataPolicy currentlySelectedMissingDataPolicy)
    {
        this.currentlySelectedMissingDataPolicy = currentlySelectedMissingDataPolicy;
    }

}
