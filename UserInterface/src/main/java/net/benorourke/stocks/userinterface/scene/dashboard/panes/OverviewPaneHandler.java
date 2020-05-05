package net.benorourke.stocks.userinterface.scene.dashboard.panes;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.collection.datasource.DataSourceManager;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.series.TimeSeriesManager;
import net.benorourke.stocks.userinterface.scene.Controller;
import net.benorourke.stocks.userinterface.scene.SceneHelper;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardController;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardModel;
import net.benorourke.stocks.userinterface.scene.dashboard.FlowStage;
import net.benorourke.stocks.userinterface.util.JavaFXUtil;

import java.util.HashMap;
import java.util.Map;

import static net.benorourke.stocks.userinterface.StockApplication.runBgThread;
import static net.benorourke.stocks.userinterface.StockApplication.runUIThread;
import static net.benorourke.stocks.userinterface.scene.dashboard.DashboardController.GENERIC_INPUT_FIELD_FXML;
import static net.benorourke.stocks.userinterface.scene.dashboard.DashboardController.PASTEL_FILLS;

public class OverviewPaneHandler extends PaneHandler
{
    private static final String[] COMBO_OPTIONS = new String[] {"Overview", "Missing / Duplicate Data", "Danger Zone"};

    private final JFXComboBox<String> overviewComboBox;
    private final TabPane overviewTabPane;

    // Overview
    private final VBox overviewDataPresentBox;
    private final PieChart overviewDistributionChart;

    // Missing / Duplicate Data
    private JFXButton overviewDuplicatesRemoveButton;

    // Danger Zone
    private JFXButton overviewDeleteButton;

    public OverviewPaneHandler(DashboardController controller, DashboardModel model,
                               JFXComboBox<String> overviewComboBox, TabPane overviewTabPane,
                               VBox overviewDataPresentBox, PieChart overviewDistributionChart,
                               JFXButton overviewDuplicatesRemoveButton, JFXButton overviewDeleteButton)
    {
        super(controller, model);

        this.overviewComboBox = overviewComboBox;
        this.overviewTabPane = overviewTabPane;
        this.overviewDataPresentBox = overviewDataPresentBox;
        this.overviewDistributionChart = overviewDistributionChart;
        this.overviewDuplicatesRemoveButton = overviewDuplicatesRemoveButton;
        this.overviewDeleteButton = overviewDeleteButton;
    }

    @Override
    public void initialise()
    {
        // POPULATE TAB PANE
        overviewComboBox.getItems().addAll(COMBO_OPTIONS);
        overviewComboBox.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            for (int i = 0; i < COMBO_OPTIONS.length; i ++)
            {
                if (COMBO_OPTIONS[i].equals(newValue))
                {
                    overviewTabPane.getSelectionModel().select(i);
                    break;
                }
            }
        });
        // Show Overview to begin with
        overviewComboBox.getSelectionModel().select(0);

        // DUPLICATES / MISSING DATA
        overviewDuplicatesRemoveButton.setOnMouseClicked(event -> onDuplicatesRemoveClicked());

        overviewDeleteButton.setOnMouseClicked(event -> onDeleteButtonClicked());
    }

    @Override
    public void onTimeSeriesChanged(TimeSeries series)
    {
        updateDataPresent(series);
    }

    @Override
    public FlowStage getNavigationRequirement()
    {
        return null;
    }

    //////////////////////////////////////////////////////////////////
    //      OVERVIEW
    //////////////////////////////////////////////////////////////////

    public void updateDataPresent(TimeSeries series)
    {
        overviewDataPresentBox.getChildren().clear();
        overviewDistributionChart.getData().clear();
        for (Map.Entry<Class<? extends DataSource>, Integer> entry : series.getRawDataCounts().entrySet())
        {
            final Class<? extends DataSource> clazz = entry.getKey();
            final int count = entry.getValue();
            runBgThread(framework ->
            {
                final DataSource src = framework.getDataSourceManager().getDataSourceByClass(clazz);
                if (src == null) return;

                runUIThread(() -> inflateCollectionDataSync(src, count));
            });
        }
    }

    public void inflateCollectionDataSync(DataSource src, int count)
    {
        SceneHelper.inflateAsync(GENERIC_INPUT_FIELD_FXML, result ->
        {
            if (!result.isSuccess()) return;

            FXMLLoader loader = result.getLoader();
            Parent parent = result.getLoaded();

            Label dataType = (Label) loader.getNamespace().get("header");
            dataType.setText(src.getName());

            Pane contentPane = (Pane) loader.getNamespace().get("contentPane");
            Label label = new Label(String.valueOf(count));
            contentPane.getChildren().add(label);

            overviewDataPresentBox.getChildren().add(parent);
        });

        // Add the slice to the chart
        PieChart.Data slice = new PieChart.Data(src.getName(), count);
        overviewDistributionChart.getData().add(slice);
        // Set the colour of this slice so it's colour is consistent
        int index = src.hashCode();
        index = (index < 0) ? index * -1 : index;
        Color color = PASTEL_FILLS[index % 10];
        slice.getNode().setStyle("-fx-pie-color: " + JavaFXUtil.toRGB(color) + ";");
    }

    //////////////////////////////////////////////////////////////////
    //      MISSING / DUPLICATE
    //////////////////////////////////////////////////////////////////

    private void onDuplicatesRemoveClicked()
    {
        final TimeSeries series = model.getCurrentlySelectedTimeSeries();
        if(series == null)
        {
            controller.snackbarNullTimeSeries();
            return;
        }

        runBgThread(framework ->
        {
            DataSourceManager sourceManager = framework.getDataSourceManager();
            TimeSeriesManager seriesManager = framework.getTimeSeriesManager();

            Map<DataSource, Integer> cleanResults = new HashMap<>();
            for (DataSource source : sourceManager.getDataSources())
            {
                int cleaned = seriesManager.cleanDuplicateData(series, source);

                if (cleaned != 0)
                    cleanResults.put(source, cleaned);
            }

            runUIThread(() ->
            {
                int total = cleanResults.values().stream().mapToInt(i -> i).sum();
                controller.snackbar(Controller.SnackbarType.INFO, "Removed " + total + " duplicate data");

                // Ensure they're still viewing the same series before we update
                if (model.getCurrentlySelectedTimeSeries() != null
                        && model.getCurrentlySelectedTimeSeries().equals(series))
                    updateDataPresent(series);
            });
        });
    }

    //////////////////////////////////////////////////////////////////
    //      DANGER ZONE
    //////////////////////////////////////////////////////////////////

    private void onDeleteButtonClicked()
    {
        TimeSeries series = model.getCurrentlySelectedTimeSeries();
        if(series == null)
        {
            controller.snackbarNullTimeSeries();
            return;
        }

        runBgThread(framework ->
        {
            framework.getTimeSeriesManager().delete(series);

            runUIThread(() ->
            {
                controller.updateTimeSeries();
                // Take them to the first tab in the Overview navigation pane
                overviewComboBox.getSelectionModel().select(0);
                model.setCurrentlySelectedTimeSeries(null);
                controller.snackbar(Controller.SnackbarType.INFO, "Successfully deleted " + series.getName());
            });
        });
    }

}
