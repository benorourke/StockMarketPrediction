package net.benorourke.stocks.userinterface.scene.dashboard;

import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.model.ModelEvaluation;
import net.benorourke.stocks.framework.model.ModelHandlerManager;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.util.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    // TRAINING
    private List<ModelHandlerManager.RuntimeCreator> modelHandlerCreators;

    // EVALUATION
    private List<String> trainedModels;
    private ModelEvaluation lastAcquiredEvaluation;

    protected DashboardModel(DashboardController controller)
    {
        this.controller = controller;
        timeSeries = new ArrayList<>();
        dataSources = new ArrayList<>();
        modelHandlerCreators = new ArrayList<>();
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
                timeSeries.clear();
                timeSeries.addAll(clone);
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
                dataSources.clear();
                dataSources.addAll(clone);
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
                modelHandlerCreators.clear();
                modelHandlerCreators.addAll(creators);
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
                trainedModels.clear();
                trainedModels.addAll(trained);
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

}
