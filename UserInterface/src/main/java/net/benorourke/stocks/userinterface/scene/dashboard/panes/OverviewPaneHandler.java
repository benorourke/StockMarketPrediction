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
import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.collection.datasource.DataSourceManager;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.series.TimeSeriesManager;
import net.benorourke.stocks.userinterface.scene.Controller;
import net.benorourke.stocks.userinterface.scene.SceneHelper;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardController;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardModel;
import net.benorourke.stocks.userinterface.scene.dashboard.FlowStage;

import java.util.HashMap;
import java.util.Map;

import static net.benorourke.stocks.userinterface.StockApplication.runBgThread;
import static net.benorourke.stocks.userinterface.StockApplication.runUIThread;
import static net.benorourke.stocks.userinterface.scene.dashboard.DashboardController.GENERIC_INPUT_FIELD_FXML;

public class OverviewPaneHandler extends PaneHandler
{
    private static final String[] COMBO_OPTIONS = new String[] {"Overview", "Missing / Duplicate Data"};

    private final JFXComboBox<String> overviewComboBox;
    private final TabPane overviewTabPane;

    // Overview
    private final VBox overviewDataPresentBox;
    private final PieChart overviewDistributionChart;

    // Missing / Duplicate Data
    private JFXButton overviewDuplicatesRemoveButton;

    public OverviewPaneHandler(DashboardController controller, DashboardModel model,
                               JFXComboBox<String> overviewComboBox, TabPane overviewTabPane,
                               VBox overviewDataPresentBox, PieChart overviewDistributionChart,
                               JFXButton overviewDuplicatesRemoveButton)
    {
        super(controller, model);

        this.overviewComboBox = overviewComboBox;
        this.overviewTabPane = overviewTabPane;
        this.overviewDataPresentBox = overviewDataPresentBox;
        this.overviewDistributionChart = overviewDistributionChart;
        this.overviewDuplicatesRemoveButton = overviewDuplicatesRemoveButton;
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

        // Update the PieChart
        PieChart.Data slice = new PieChart.Data(src.getName(), count);
        overviewDistributionChart.getData().add(slice);
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

}