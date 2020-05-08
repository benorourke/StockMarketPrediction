package net.benorourke.stocks.userinterface.scene.dashboard.panes;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.exception.InsuficcientRawDataException;
import net.benorourke.stocks.framework.exception.TaskStartException;
import net.benorourke.stocks.framework.model.ProcessedDataset;
import net.benorourke.stocks.framework.persistence.store.DataStore;
import net.benorourke.stocks.framework.preprocess.FeatureRepresentor;
import net.benorourke.stocks.framework.preprocess.FeatureRepresentorManager;
import net.benorourke.stocks.framework.preprocess.assignment.AverageDataMapper;
import net.benorourke.stocks.framework.preprocess.assignment.MissingDataPolicy;
import net.benorourke.stocks.framework.preprocess.assignment.ModelDataMapper;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.impl.CleanedDocument;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;
import net.benorourke.stocks.framework.series.data.impl.StockQuoteDataType;
import net.benorourke.stocks.framework.thread.ResultCallback;
import net.benorourke.stocks.framework.thread.preprocessing.PreprocessingResult;
import net.benorourke.stocks.framework.thread.preprocessing.PreprocessingTask;
import net.benorourke.stocks.userinterface.StockApplication;
import net.benorourke.stocks.userinterface.scene.Controller;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardController;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardModel;
import net.benorourke.stocks.userinterface.scene.dashboard.FlowStage;
import net.benorourke.stocks.userinterface.util.Constants;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PreprocessingHandler extends PaneHandler
{
    private static final StockQuoteDataType[] LABELS_TO_PREDICT = new StockQuoteDataType[] { StockQuoteDataType.CLOSE };

    private final VBox preprocessingTogglesBox;
    private final Map<JFXToggleButton, FeatureRepresentor> toggleMappings;

    private final JFXComboBox preprocessingPolicyBox;

    private final JFXButton preprocessingBegin;

    public PreprocessingHandler(DashboardController controller, DashboardModel model, VBox preprocessingTogglesBox,
                                JFXComboBox preprocessingPolicyBox, JFXButton preprocessingBegin)
    {
        super(controller, model);

        this.preprocessingTogglesBox = preprocessingTogglesBox;
        this.toggleMappings = new LinkedHashMap<>();

        this.preprocessingPolicyBox = preprocessingPolicyBox;

        this.preprocessingBegin = preprocessingBegin;
    }

    @Override
    public void initialise()
    {
        model.acquireFeatureRepresentionData(() ->
        {
            populateTogglesBox();
            populatePolicyBox();
        });

        preprocessingBegin.setOnMouseClicked(event -> beginProcessing(model.getCurrentlySelectedTimeSeries()));
    }

    @Override
    public void onTimeSeriesChanged(TimeSeries series)
    {

    }

    @Override
    public FlowStage getNavigationRequirement()
    {
        return FlowStage.COLLECTING_AND_INJECTING;
    }

    @Override
    public void onSwitchedTo() { }

    private void populateTogglesBox()
    {
        preprocessingTogglesBox.getChildren().clear();
        toggleMappings.clear();

        createToggleMappings(model.getQuoteFeatureRepresentors());
        createToggleMappings(model.getDocumentFeatureRepresentors());
    }

    private <T extends Data> void createToggleMappings(
            Map<FeatureRepresentorManager.Metadata, FeatureRepresentor<T>> representors)
    {
        for (Map.Entry<FeatureRepresentorManager.Metadata, FeatureRepresentor<T>> entry : representors.entrySet())
        {
            JFXToggleButton toggle = new JFXToggleButton();
            toggle.setText(entry.getKey().getName());
            Tooltip tooltip = new Tooltip("Description: " + entry.getKey().getDescription()
                                            + "\n(Estimated) Vector Width: " + entry.getKey().getEstimatedVectorWidth());
            toggle.setTooltip(tooltip);
            preprocessingTogglesBox.getChildren().add(toggle);
            toggleMappings.put(toggle, entry.getValue());
        }
    }

    private void populatePolicyBox()
    {
        preprocessingPolicyBox.getItems().clear();
        final List<String> boxItems = model.getMissingDataPolicies()
                                                .stream()
                                                .map(p -> p.getName())
                                                .collect(Collectors.toList());
        preprocessingPolicyBox.getItems().addAll(boxItems);

        // Keep the current instance of the policies, just in case they get changed, to prevent a potential
        // IndexOutOfBoundsException
        final List<MissingDataPolicy> clone = Collections.unmodifiableList(model.getMissingDataPolicies());
        preprocessingPolicyBox.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            MissingDataPolicy selected = clone.get(boxItems.indexOf(newValue));
            model.setCurrentlySelectedMissingDataPolicy(selected);
        });

        // There's always at least one:
        preprocessingPolicyBox.getSelectionModel().select(0);
    }

    private void beginProcessing(final TimeSeries series)
    {
        if(series == null)
        {
            controller.snackbarNullTimeSeries();
            return;
        }

        final List<FeatureRepresentor<StockQuote>> quoteRepresentors = new ArrayList<>();
        final List<FeatureRepresentor<CleanedDocument>> documentRepresentors = new ArrayList<>();

        for (Map.Entry<JFXToggleButton, FeatureRepresentor> entry : toggleMappings.entrySet())
        {
            if (!entry.getKey().isSelected())
                continue;

            FeatureRepresentor representor = entry.getValue();
            if (representor.getTypeFor().equals(DataType.STOCK_QUOTE))
                quoteRepresentors.add((FeatureRepresentor<StockQuote>) representor);
            else if (representor.getTypeFor().equals(DataType.CLEANED_DOCUMENT))
                documentRepresentors.add((FeatureRepresentor<CleanedDocument>) representor);
        }

        final MissingDataPolicy policy = model.getCurrentlySelectedMissingDataPolicy();

        StockApplication.runBgThread(framework ->
        {
            try
            {
                PreprocessingTask task = createTask(framework, series, quoteRepresentors, documentRepresentors, policy);
                framework.getTaskManager().scheduleRepeating(task, new ResultCallback<PreprocessingResult>()
                {
                    @Override
                    public void onCallback(PreprocessingResult result)
                    {
                        File file = framework.getFileManager().getProcessedCorpusFile(series);
                        ProcessedDataset dataset = result.getDataset();
                        framework.getFileManager().writeJson(file, dataset);

                        StockApplication.runUIThread(() ->
                                controller.snackbar(Controller.SnackbarType.INFO,
                                                    "Successfully pre-processed and wrote to file"));

                        // Resolve the flow stage so if they were to click train models they'd be able to
                        model.resolveFlowStage(series, () -> {});
                    }
                }, Constants.PREPROCESSING_DELAY, Constants.PREPROCESSING_INTERVAL, TimeUnit.MILLISECONDS);
            }
            catch (final InsuficcientRawDataException insufficientException)
            {
                StockApplication.runUIThread(() ->
                {
                    String types = insufficientException.getMissing()
                                            .stream()
                                            .map(DataType::getName)
                                            .collect(Collectors.joining(", "));
                    controller.snackbar(Controller.SnackbarType.ERROR,
                                        "Unable to begin pre-processing due to missing data types: " + types);
                });
            }

            catch (TaskStartException e)
            {
                StockApplication.runUIThread(() ->
                {
                    controller.snackbar(Controller.SnackbarType.ERROR, e.getMessage());
                });
            }
        });
    }


    /**
     * Run from the Background (Framework) Thread
     *
     * @return
     */
    private PreprocessingTask createTask(Framework framework, TimeSeries series,
                                         List<FeatureRepresentor<StockQuote>> quoteRepresentors,
                                         List<FeatureRepresentor<CleanedDocument>> documentRepresentors,
                                         MissingDataPolicy policy) throws InsuficcientRawDataException
    {
        DataStore store = framework.getTimeSeriesManager().getDataStore(series);
        Map<DataSource, Integer> dataCounts = new HashMap<>();
        for (Map.Entry<Class<? extends DataSource>, Integer> entry : series.getRawDataCounts().entrySet())
        {
            DataSource source = framework.getDataSourceManager().getDataSourceByClass(entry.getKey());
            if (source == null)
            {
                StockApplication.error("Unable to resolve DataSource " + entry.getKey().toString());
                continue;
            }

            dataCounts.put(source, entry.getValue());
        }
        ModelDataMapper mapper = new AverageDataMapper(documentRepresentors, quoteRepresentors, LABELS_TO_PREDICT);

        return new PreprocessingTask(store, dataCounts, documentRepresentors, quoteRepresentors, policy, mapper);
    }

}
