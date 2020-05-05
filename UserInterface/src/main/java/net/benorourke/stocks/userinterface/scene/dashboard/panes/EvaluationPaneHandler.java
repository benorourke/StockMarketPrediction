package net.benorourke.stocks.userinterface.scene.dashboard.panes;

import com.jfoenix.controls.JFXComboBox;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import net.benorourke.stocks.framework.model.ModelEvaluation;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.util.DateUtil;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardController;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardModel;
import net.benorourke.stocks.userinterface.scene.dashboard.FlowStage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EvaluationPaneHandler extends PaneHandler
{
    private static final int LABEL_COL = 0;

    private JFXComboBox<String> evaluationComboBox;
    private LineChart<String, Number> chart;

    public EvaluationPaneHandler(DashboardController controller, DashboardModel model,
                                 JFXComboBox<String> evaluationComboBox, LineChart<String, Number> chart)
    {
        super(controller, model);

        this.evaluationComboBox = evaluationComboBox;
        this.chart = chart;
    }

    @Override
    public void initialise()
    {
        chart.getYAxis().setLabel("Stock Value");
    }

    @Override
    public void onTimeSeriesChanged(TimeSeries series)
    {
        model.acquireTrainedModels(series, () -> updateOptions(series, model.getTrainedModels()));
    }

    @Override
    public FlowStage getNavigationRequirement()
    {
        return FlowStage.TRAINING_AND_EVALUATING_MODELS;
    }

    public void updateOptions(TimeSeries series, List<String> trainedModels)
    {
        evaluationComboBox.getSelectionModel().clearSelection();
        evaluationComboBox.getItems().clear();
        evaluationComboBox.getItems().addAll(trainedModels);

        evaluationComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
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
        predictions.addAll(evaluation.getTrainingPredictions());
        predictions.addAll(evaluation.getTestingPredictions());
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

}
