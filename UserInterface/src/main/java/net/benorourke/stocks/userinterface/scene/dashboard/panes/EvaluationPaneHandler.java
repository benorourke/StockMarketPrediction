package net.benorourke.stocks.userinterface.scene.dashboard.panes;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import net.benorourke.stocks.framework.model.ModelEvaluation;
import net.benorourke.stocks.framework.persistence.FileManager;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.util.DateUtil;
import net.benorourke.stocks.userinterface.scene.Controller;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardController;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardModel;
import net.benorourke.stocks.userinterface.scene.dashboard.FlowStage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static net.benorourke.stocks.userinterface.StockApplication.runBgThread;
import static net.benorourke.stocks.userinterface.StockApplication.runUIThread;

public class EvaluationPaneHandler extends PaneHandler
{
    private static final int LABEL_COL = 0;

    private final JFXComboBox<String> evaluationComboBox;
    private final LineChart<String, Number> chart;
    private final JFXCheckBox evaluationTrainingCheckBox;
    private final JFXCheckBox evaluationTestingCheckBox;
    private final JFXButton evaluationDeleteButton;

    public EvaluationPaneHandler(DashboardController controller, DashboardModel model,
                                 JFXComboBox<String> evaluationComboBox, LineChart<String, Number> chart,
                                 JFXCheckBox evaluationTrainingCheckBox, JFXCheckBox evaluationTestingCheckBox,
                                 JFXButton evaluationDeleteButton)
    {
        super(controller, model);

        this.evaluationComboBox = evaluationComboBox;
        this.chart = chart;
        this.evaluationTrainingCheckBox = evaluationTrainingCheckBox;
        this.evaluationTestingCheckBox = evaluationTestingCheckBox;
        this.evaluationDeleteButton = evaluationDeleteButton;
    }

    @Override
    public void initialise()
    {
        chart.getYAxis().setLabel("Stock Value");
        chart.setAnimated(false);

        evaluationTrainingCheckBox.selectedProperty().addListener(
                (observable, oldValue, newValue) -> onCheckBoxChanged());
        evaluationTestingCheckBox.selectedProperty().addListener(
                (observable, oldValue, newValue) -> onCheckBoxChanged());

        evaluationDeleteButton.setOnMouseClicked(event -> onDeleteClicked());
    }

    @Override
    public void onTimeSeriesChanged(TimeSeries series)
    {
        onModelsChanged(series);
    }

    @Override
    public FlowStage getNavigationRequirement()
    {
        return FlowStage.TRAINING_AND_EVALUATING_MODELS;
    }

    @Override
    public void onSwitchedTo() { }

    public void onModelsChanged(TimeSeries series)
    {
        model.acquireTrainedModels(series, () -> updateOptions(series, model.getTrainedModels()));
    }

    public void updateOptions(TimeSeries series, List<String> trainedModels)
    {
        evaluationComboBox.getSelectionModel().clearSelection();
        evaluationComboBox.getItems().clear();
        evaluationComboBox.getItems().addAll(trainedModels);

        evaluationComboBox.valueProperty().addListener((observable, oldValue, newValue) ->
        {
                if (newValue == null) return;

                model.acquireEvaluation(series, newValue, () -> plotGraph(model.getLastAcquiredEvaluation(), LABEL_COL));
        });

        // Select the first
        if (evaluationComboBox.getItems().size() > 0)
            evaluationComboBox.getSelectionModel().select(0);
    }

    public void plotGraph(ModelEvaluation evaluation, int column)
    {
        XYChart.Series<String, Number> predictedSeries = new XYChart.Series<>();
        predictedSeries.setName("Predicted");
        XYChart.Series<String, Number> labelSeries = new XYChart.Series<>();
        labelSeries.setName("Actual");

        List<ModelEvaluation.Prediction> predictions = new ArrayList<>();
        if (evaluationTrainingCheckBox.isSelected()) predictions.addAll(evaluation.getTrainingPredictions());
        if (evaluationTestingCheckBox.isSelected()) predictions.addAll(evaluation.getTestingPredictions());
        Collections.sort(predictions, Comparator.comparing(ModelEvaluation.Prediction::getDate));
        for (ModelEvaluation.Prediction prediction : predictions)
        {
            String date = DateUtil.formatSimple(prediction.getDate());

            predictedSeries.getData().add(new XYChart.Data<>(date, prediction.getPredicted()[column]));
            labelSeries.getData().add(new XYChart.Data<>(date, prediction.getLabels()[column]));
        }

        chart.getData().clear();
        chart.getData().addAll(predictedSeries, labelSeries);
    }

    private void onCheckBoxChanged()
    {
        ModelEvaluation lastAcquired = model.getLastAcquiredEvaluation();
        if (lastAcquired == null)
            return;

        plotGraph(lastAcquired, LABEL_COL);
    }

    private void onDeleteClicked()
    {
        ModelEvaluation lastAcquired = model.getLastAcquiredEvaluation();
        if (lastAcquired == null)
            return;

        TimeSeries series = model.getCurrentlySelectedTimeSeries();
        if (series == null) // this should never happen, but in case it does
        {
            controller.snackbarNullTimeSeries();
            return;
        }

        final String name = model.getLastAcquiredEvaluationName();
        runBgThread(framework ->
        {
            FileManager fileManager = framework.getFileManager();
            File evalFile = fileManager.getModelEvaluationFile(series, name);
            File modelFile = fileManager.getModelFile(series, name);

            evalFile.delete();
            modelFile.delete();

            runUIThread(() ->
            {
                chart.getData().clear();
                model.acquireTrainedModels(series, () -> updateOptions(series, model.getTrainedModels()));
                controller.snackbar(Controller.SnackbarType.INFO, "Successfully Deleted Model " + name);
            });
        });
    }

}
