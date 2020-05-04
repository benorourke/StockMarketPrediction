package net.benorourke.stocks.userinterface.scene.dashboard.panes;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.collection.Query;
import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.collection.datasource.variable.CollectionVariable;
import net.benorourke.stocks.framework.collection.session.filter.CollectionFilter;
import net.benorourke.stocks.framework.exception.TaskStartException;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.series.TimeSeriesManager;
import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.impl.Document;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;
import net.benorourke.stocks.framework.thread.collection.CollectionTask;
import net.benorourke.stocks.framework.util.Nullable;
import net.benorourke.stocks.framework.util.Tuple;
import net.benorourke.stocks.userinterface.scene.Controller;
import net.benorourke.stocks.userinterface.scene.SceneHelper;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardController;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardModel;
import net.benorourke.stocks.userinterface.util.Constants;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static net.benorourke.stocks.userinterface.StockApplication.runBgThread;
import static net.benorourke.stocks.userinterface.StockApplication.runUIThread;
import static net.benorourke.stocks.userinterface.scene.dashboard.DashboardController.GENERIC_INPUT_FIELD_FXML;
import static net.benorourke.stocks.userinterface.scene.dashboard.DashboardController.GENERIC_INPUT_FIELD_WIDTH_BIND_COEFF;

public class CollectionPaneHandler extends PaneHandler
{
    // Collect Data
    private final JFXComboBox collectionCollectSourceComboBox;
    private final JFXDatePicker collectionCollectDataPickerFrom, collectionCollectDataPickerTo;
    private final VBox collectionCollectBox;
    private final JFXButton collectionCollectButton;
    /** First is always the API Key */
    @Nullable
    private Parent[] collectionCollectionBoxVariables;

    public CollectionPaneHandler(DashboardController controller, DashboardModel model,
                                 JFXComboBox collectionCollectSourceComboBox,
                                 JFXDatePicker collectionCollectDataPickerFrom, JFXDatePicker collectionCollectDataPickerTo,
                                 VBox collectionCollectBox, JFXButton collectionCollectButton)
    {
        super(controller, model);

        this.collectionCollectSourceComboBox = collectionCollectSourceComboBox;
        this.collectionCollectDataPickerFrom = collectionCollectDataPickerFrom;
        this.collectionCollectDataPickerTo = collectionCollectDataPickerTo;
        this.collectionCollectBox = collectionCollectBox;
        this.collectionCollectButton = collectionCollectButton;
    }

    @Override
    public void initialise()
    {
        // COLLECTION
        collectionCollectDataPickerTo.setValue(LocalDate.now());

        collectionCollectButton.setOnMouseClicked(event -> onCollectClicked(model.getCurrentlySelectedTimeSeries(),
                model.getCurrentlySelectedCollectionDataSource()));
        model.acquireDataSources(() ->
        {
            List<DataSource> sources = model.getDataSources();

            // Populate all of the collection sources to choose from
            List<String> strSources = sources.stream()
                    .map(s -> s.getName() + " (" + s.getDataType().getName() + ")")
                    .collect(Collectors.toList());
            collectionCollectSourceComboBox.getItems().addAll(strSources);
            // Change the current collection source to whatever they just selected
            collectionCollectSourceComboBox.valueProperty().addListener((observable, oldValue, newValue) ->
                    selectCollectionDataSource(sources.get(strSources.indexOf(newValue))));

            // Select the first source - there's several inbuilt ones so this is safe
            collectionCollectSourceComboBox.getSelectionModel().select(0);
        });
    }

    @Override
    public void onTimeSeriesChanged(TimeSeries series) { }

    //////////////////////////////////////////////////////////////////
    //      COLLECT
    //////////////////////////////////////////////////////////////////

    private void selectCollectionDataSource(DataSource source)
    {
        model.setCurrentlySelectedCollectionDataSource(source);
        populateCollectionCollectBox(source);
    }

    public void populateCollectionCollectBox(DataSource source)
    {
        Set<CollectionVariable> variables = source.getCollectionVariables();

        collectionCollectBox.getChildren().clear();
        collectionCollectionBoxVariables = new Parent[variables.size()];

        int index = 0;
        for (CollectionVariable variable : variables)
            inflateCollectionCollectBoxVariable(source, variable, index ++);
    }

    private void inflateCollectionCollectBoxVariable(DataSource source, CollectionVariable variable, int index)
    {
        SceneHelper.inflateAsync(GENERIC_INPUT_FIELD_FXML, result ->
        {
            if (!result.isSuccess()) return;

            FXMLLoader loader = result.getLoader();
            Parent parent = result.getLoaded();

            Label label = (Label) loader.getNamespace().get("header");
            label.setText(variable.name());

            Pane contentPane = (Pane) loader.getNamespace().get("contentPane");

            switch (variable.type())
            {
                case INTEGER:
                case STRING:
                    contentPane.getChildren().add(createCollectionCollectBoxTextField(contentPane, variable.prompt()));
                    break;
            }

            collectionCollectionBoxVariables[index] = parent;

            // We'll only add all the fields once all of the parents have been set
            if (checkCollectVariableFieldsLoaded())
                for (int i = 0; i < collectionCollectionBoxVariables.length; i ++)
                    collectionCollectBox.getChildren().add(collectionCollectionBoxVariables[i]);
        });
    }

    private JFXTextField createCollectionCollectBoxTextField(Pane parent, String prompt)
    {
        JFXTextField textField = new JFXTextField();
        textField.setPromptText(prompt);
        textField.prefWidthProperty().bind(parent.prefWidthProperty().multiply(GENERIC_INPUT_FIELD_WIDTH_BIND_COEFF));
        return textField;
    }

    /**
     * Types of errors that can arise:
     * 1)
     * 2)
     * 3)
     * 4)
     * 5)
     *
     * @param source
     */
    private void onCollectClicked(@Nullable TimeSeries series, DataSource source)
    {
        if(series == null)
        {
            controller.snackbarNullTimeSeries();
            return;
        }
        if (!checkCollectVariableFieldsLoaded()) return;
        if (!checkCollectSetVariables(source)) return;
        if (!checkCollectVariablesValid(source)) return;
        if (!checkCollectQuerySpecified()) return;

        Tuple<Date, Date> queryDates = getCollectQuerySpecified();
        Query query = new Query(queryDates.getB(), queryDates.getA());

        DataType type = source.getDataType();
        CollectionTask task;
        if (type.equals(DataType.DOCUMENT))
            task = createCollectionTask((DataSource<Document>) source, query,
                    (CollectionFilter<Document>) source.newDefaultCollectionFilter());
        else if(type.equals(DataType.STOCK_QUOTE))
            task = createCollectionTask((DataSource<StockQuote>) source, query,
                    (CollectionFilter<StockQuote>) source.newDefaultCollectionFilter());
        else
        {
            controller.snackbar(Controller.SnackbarType.ERROR, "Unknown Collection Data Type" + type.getName());
            return;
        }

        beginCollectionTask(task, series, source);
    }

    private <T extends Data> CollectionTask<T> createCollectionTask(DataSource<T> source, Query query,
                                                                    CollectionFilter<T> collectionFilter)
    {
        return new CollectionTask<>(source, source.newSession(query, collectionFilter));
    }

    private <T extends Data> void beginCollectionTask(CollectionTask<T> task, TimeSeries series, DataSource<T> source)
    {
        runBgThread(framework ->
        {
            try
            {
                framework.getTaskManager().scheduleRepeating(task, result ->
                {
                    final List<T> data = result.getData();

                    TimeSeriesManager manager = framework.getTimeSeriesManager();
                    // Write the data to the file
                    manager.onDataCollected(series, source, data, false);

                    runUIThread(() ->
                    {
                        Framework.info("Collected data, cardinality: " + data.size());

                        // Update the data count in the data present overview tab to reflect these changes
                        ((OverviewPaneHandler) controller.getPaneHandler(DashboardPane.OVERVIEW)).updateDataPresent(series);
                    });
                }, Constants.COLLECTION_DELAY, Constants.COLLECTION_INTERVAL, TimeUnit.MILLISECONDS);
            }
            catch (TaskStartException e)
            {
                runUIThread(() ->
                {
                    controller.snackbar(Controller.SnackbarType.ERROR,
                            "Unable to schedule CollectionTask " + e.getMessage());
                });
            }
        });
    }

    public boolean checkCollectVariableFieldsLoaded()
    {
        for (int i = 0 ; i < collectionCollectionBoxVariables.length; i ++)
        {
            if (collectionCollectionBoxVariables[i] == null) return false;
        }

        return true;
    }

    public String getCollectAPIKey()
    {
        Parent parent = collectionCollectionBoxVariables[0];
        Parent vbox = (Parent) parent.getChildrenUnmodifiable().get(0);
        Pane contentPane = (Pane) ((HBox) vbox.getChildrenUnmodifiable().get(1)).getChildrenUnmodifiable().get(0);
        return ((JFXTextField) contentPane.getChildren().get(0)).getText();
    }

    private boolean checkCollectSetVariables(DataSource source)
    {
        Set<CollectionVariable> variables = source.getCollectionVariables();
        int index = 0;
        for (CollectionVariable variable : variables)
        {
            Parent parent = collectionCollectionBoxVariables[index ++];
            Parent vbox = (Parent) parent.getChildrenUnmodifiable().get(0);
            Pane contentPane = (Pane) ((HBox) vbox.getChildrenUnmodifiable().get(1))
                    .getChildrenUnmodifiable().get(0);

            switch (variable.type())
            {
                case INTEGER:
                    JFXTextField intTextField = (JFXTextField) contentPane.getChildren().get(0);
                    try { source.setVariableValue(variable, Integer.parseInt(intTextField.getText())); }
                    catch (Exception e)
                    {
                        controller.snackbar(Controller.SnackbarType.ERROR,
                                "Unable to parse " + intTextField.getText() + ", is it numeric?");
                        return false;
                    }
                    break;
                case STRING:
                    JFXTextField strTextField = (JFXTextField) contentPane.getChildren().get(0);
                    try { source.setVariableValue(variable, strTextField.getText()); }
                    catch (Exception e)
                    {
                        controller.snackbar(Controller.SnackbarType.ERROR,
                                "Unable to parse " + strTextField.getText());
                        return false;
                    }
                    break;
            }
        }

        return true;
    }

    private boolean checkCollectVariablesValid(DataSource source)
    {
        String validationResult = source.validateCollectionVariables();
        if (validationResult != null)
        {
            controller.snackbar(Controller.SnackbarType.ERROR,
                    "Unable to validate collection variable: " + validationResult);
            return false;
        }
        return true;
    }

    private boolean checkCollectQuerySpecified()
    {
        Tuple<Date, Date> specified = getCollectQuerySpecified();

        if (specified.getA() == null)
        {
            controller.snackbar(Controller.SnackbarType.ERROR, "Specify a date from which to collect!");
            return false;
        }
        if (specified.getB() == null)
        {
            controller.snackbar(Controller.SnackbarType.ERROR, "Specify a date to collect until!");
            return false;
        }
        return true;
    }

    private Tuple<Date, Date> getCollectQuerySpecified()
    {
        LocalDate from = collectionCollectDataPickerFrom.getValue();
        LocalDate to = collectionCollectDataPickerTo.getValue();

        return new Tuple<>( (from == null) ? null : Date.from(from.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                (to == null)   ? null : Date.from(to.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }
}
