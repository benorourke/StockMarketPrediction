package net.benorourke.stocks.userinterface.scene.dashboard.panes;

import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardController;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardModel;

import java.util.List;

public class EvaluationPaneHandler extends PaneHandler
{
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
        XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
        for (int i = 0; i < 150; i ++)
        {
            series.getData().add(new XYChart.Data<String, Number>("" + i, i*2 + 5));
        }
        chart.getData().add(series);
    }

    @Override
    public void onTimeSeriesChanged(TimeSeries series)
    {
        model.acquireTrainedModels(series, () -> updateOptions(model.getTrainedModels()));
    }

    public void updateOptions(List<String> trainedModels)
    {
        evaluationComboBox.getSelectionModel().clearSelection();
        evaluationComboBox.getItems().clear();
        evaluationComboBox.getItems().addAll(trainedModels);
    }

}
