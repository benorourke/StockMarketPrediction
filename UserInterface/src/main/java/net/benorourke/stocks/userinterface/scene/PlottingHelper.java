package net.benorourke.stocks.userinterface.scene;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import net.benorourke.stocks.framework.util.Nullable;
import net.benorourke.stocks.framework.util.Tuple;

import java.util.List;

public class PlottingHelper
{

    private PlottingHelper() {}

    public static LineChart<Number, Number> plot(List<List<Tuple<Number, Number>>> lines, @Nullable String title,
                                                 @Nullable String xLabel, @Nullable String yLabel)
    {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        if (xLabel != null)
            xAxis.setLabel(xLabel);
        if (yLabel != null)
            yAxis.setLabel(yLabel);

        final LineChart<Number,Number> chart = new LineChart(xAxis, yAxis);
        if (title != null)
            chart.setTitle(title);

        for (List<Tuple<Number, Number>> pointsOnLine : lines)
        {
            XYChart.Series series = new XYChart.Series();
            for (Tuple<Number, Number> point : pointsOnLine)
                series.getData().add(new XYChart.Data(point.getA(), point.getB()));
            chart.getData().add(series);
        }

        return chart;
    }

//    private static <T extends PredictionModel> LineChart<Number, Number>
//                predictAndPlot(ModelHandler<T> handler, T model, ProcessedDataset data)
//    {
//
//    }

}
