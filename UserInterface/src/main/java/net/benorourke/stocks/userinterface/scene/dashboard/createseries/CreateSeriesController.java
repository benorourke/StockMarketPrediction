package net.benorourke.stocks.userinterface.scene.dashboard.createseries;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.util.Nullable;
import net.benorourke.stocks.userinterface.exception.SceneCreationDataException;
import net.benorourke.stocks.userinterface.scene.Controller;
import net.benorourke.stocks.userinterface.scene.SceneHelper;
import net.benorourke.stocks.userinterface.scene.SceneType;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardController;
import net.benorourke.stocks.userinterface.util.Constants;

import static net.benorourke.stocks.userinterface.StockApplication.runBgThread;
import static net.benorourke.stocks.userinterface.StockApplication.runUIThread;

public class CreateSeriesController extends Controller implements EventHandler<MouseEvent>
{
    /**
     * Only one CreateSeriesController instance can be open at a time
     */
    @Nullable
    private static Stage singletonInstance;
    @Nullable
    private static DashboardController dashboardController;

    @FXML private JFXTextField seriesNameField;
    @FXML private JFXTextField stockNameField;
    @FXML private JFXButton createButton;

    @FXML
    public void initialize()
    {
        createButton.setOnMouseClicked(this);
    }

    @Override
    public void handle(MouseEvent event)
    {
        if (!checkFieldLength(seriesNameField, "Enter a name for the series")) return;
        if (!checkFieldLength(stockNameField, "Enter a name and exchange to collect data on")) return;

        final String seriesName = seriesNameField.getText();
        final String stockName = stockNameField.getText();

        runBgThread(framework ->
        {
            boolean exists = framework.getTimeSeriesManager().exists(seriesName);
            if (exists)
            {
                runUIThread(() -> snackbar(SnackbarType.ERROR, "A Series with this name already exists"));
                return;
            }

            boolean success = framework.getTimeSeriesManager().create(seriesName, stockName);
            if (!success)
            {
                runUIThread(() -> snackbar(SnackbarType.ERROR, "Unable to create Time Series"));
                return;
            }

            final TimeSeries created = framework.getTimeSeriesManager().getByName(seriesName);
            runUIThread(() ->
            {
                dashboardController.inflateTimeSeries(created, true);
                dashboardController.snackbar(SnackbarType.INFO, "Time Series successfully created");
                singletonInstance.close();
                singletonInstance = null;
                dashboardController = null;
            });
        });
    }

    private boolean checkFieldLength(JFXTextField field, String errorMessage)
    {
        if (field.getText().replace(" ", "").length() == 0)
        {
            snackbar(SnackbarType.ERROR, errorMessage);
            return false;
        }
        return true;
    }

    public static void show(DashboardController controller)
    {
        if (singletonInstance == null)
        {
            dashboardController = controller;
            try
            {
                singletonInstance = SceneHelper.openStage(Constants.CREATE_SERIES_NAME,
                        Constants.CREATE_SERIES_WIDTH_MIN,
                        Constants.CREATE_SERIES_HEIGHT_MIN,
                        false, false, SceneType.CREATE_SERIES);
            }
            catch (SceneCreationDataException e) { return; }
        }

        singletonInstance.requestFocus();
    }

}
