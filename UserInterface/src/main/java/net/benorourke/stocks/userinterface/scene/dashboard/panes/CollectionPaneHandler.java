package net.benorourke.stocks.userinterface.scene.dashboard.panes;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.collection.Query;
import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.collection.datasource.variable.CollectionVariable;
import net.benorourke.stocks.framework.collection.session.filter.CollectionFilter;
import net.benorourke.stocks.framework.exception.TaskAlreadyPresentException;
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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static net.benorourke.stocks.userinterface.StockApplication.runBgThread;
import static net.benorourke.stocks.userinterface.StockApplication.runUIThread;

public class CollectionPaneHandler extends PaneHandler
{
    private static final String COLLECTION_ROW_FXML = "/dashboard-collection-row.fxml";
    private static final String[] COMBO_OPTIONS = new String[] {"Overview", "Collect Data", "Missing / Duplicate Data"};

    private final JFXComboBox<String> collectionComboBox;
    private final TabPane collectionTabPane;

    // Overview
    private final VBox collectionDataPresentBox;

    // Collect Data
    private final JFXComboBox collectionCollectSourceComboBox;
    private final JFXDatePicker collectionCollectDataPickerFrom, collectionCollectDataPickerTo;
    private final VBox collectionCollectBox;
    private final JFXButton collectionCollectButton;
    /** First is always the API Key */
    @Nullable private Parent[] collectionCollectionBoxVariables;

    public CollectionPaneHandler(DashboardController controller, DashboardModel model,
                                 JFXComboBox<String> collectionComboBox, TabPane collectionTabPane,
                                 VBox collectionDataPresentBox,
                                 JFXComboBox collectionCollectSourceComboBox,
                                 JFXDatePicker collectionCollectDataPickerFrom, JFXDatePicker collectionCollectDataPickerTo,
                                 VBox collectionCollectBox, JFXButton collectionCollectButton)
    {
        super(controller, model);

        this.collectionComboBox = collectionComboBox;
        this.collectionTabPane = collectionTabPane;
        this.collectionDataPresentBox = collectionDataPresentBox;
        this.collectionCollectSourceComboBox = collectionCollectSourceComboBox;
        this.collectionCollectDataPickerFrom = collectionCollectDataPickerFrom;
        this.collectionCollectDataPickerTo = collectionCollectDataPickerTo;
        this.collectionCollectBox = collectionCollectBox;
        this.collectionCollectButton = collectionCollectButton;
    }

    @Override
    public void initialise()
    {
        collectionComboBox.getItems().addAll(COMBO_OPTIONS);
        collectionComboBox.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            for (int i = 0; i < COMBO_OPTIONS.length; i ++)
            {
                if (COMBO_OPTIONS[i].equals(newValue))
                {
                    collectionTabPane.getSelectionModel().select(i);
                    break;
                }
            }
        });
        // Show Overview to begin with
        collectionComboBox.getSelectionModel().select(0);

        collectionCollectDataPickerTo.setValue(LocalDate.now());

        collectionCollectButton.setOnMouseClicked(event -> onCollectClicked(model.getCurrentlySelectedTimeSeries(),
                                                                            model.getCurrentlySelectedDataSource()));
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
    public void onTimeSeriesChanged(TimeSeries series)
    {
        updateDataPresent(series);
    }

    //////////////////////////////////////////////////////////////////
    //      OVERVIEW
    //////////////////////////////////////////////////////////////////

    public void updateDataPresent(TimeSeries series)
    {
        collectionDataPresentBox.getChildren().clear();
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
        SceneHelper.inflateAsync(COLLECTION_ROW_FXML, result ->
        {
            if (!result.isSuccess()) return;

            FXMLLoader loader = result.getLoader();
            Parent parent = result.getLoaded();

            Label dataType = (Label) loader.getNamespace().get("header");
            dataType.setText(src.getName());

            Pane contentPane = (Pane) loader.getNamespace().get("contentPane");
            Label label = new Label(String.valueOf(count));
            contentPane.getChildren().add(label);

            collectionDataPresentBox.getChildren().add(parent);
        });
    }

    //////////////////////////////////////////////////////////////////
    //      COLLECT
    //////////////////////////////////////////////////////////////////

    public void selectCollectionDataSource(DataSource source)
    {
        model.setCurrentlySelectedDataSource(source);
        populateCollectionCollectBox(source);
    }

    public void populateCollectionCollectBox(DataSource source)
    {
        Set<CollectionVariable> variables = source.getCollectionVariables();

        // We will use the existing parent for the apiKey if it has already been inflated,
        // as this is DataSource independent.
        Parent apiKey = (collectionCollectionBoxVariables == null) ? null : collectionCollectionBoxVariables[0];

        // Remove every child apart from the apiKey (if the children exist yet)
        if (collectionCollectBox.getChildren().size() > 1)
            collectionCollectBox.getChildren().remove(1, collectionCollectBox.getChildren().size());
        // + 1 for the API key
        collectionCollectionBoxVariables = new Parent[1 + variables.size()];

        if (apiKey == null)
            inflateCollectionCollectBoxAPIKey();
        else
            collectionCollectionBoxVariables[0] = apiKey;

        int index = 1;
        for (CollectionVariable variable : variables)
            inflateCollectionCollectBoxVariable(source, variable, index ++);
    }

    private void inflateCollectionCollectBoxAPIKey()
    {
        SceneHelper.inflateAsync(COLLECTION_ROW_FXML, result ->
        {
            if (!result.isSuccess()) return;

            FXMLLoader loader = result.getLoader();
            Parent parent = result.getLoaded();

            Label dataType = (Label) loader.getNamespace().get("header");
            dataType.setText("API Key");

            Pane contentPane = (Pane) loader.getNamespace().get("contentPane");

            JFXTextField textField = createCollectionCollectBoxTextField(contentPane, "DataSource API Key");
            contentPane.getChildren().add(textField);

            collectionCollectionBoxVariables[0] = parent;
            collectionCollectBox.getChildren().add(parent);
        });
    }

    private void inflateCollectionCollectBoxVariable(DataSource source, CollectionVariable variable, int index)
    {
        SceneHelper.inflateAsync(COLLECTION_ROW_FXML, result ->
        {
            if (!result.isSuccess()) return;

            FXMLLoader loader = result.getLoader();
            Parent parent = result.getLoaded();

            Label dataType = (Label) loader.getNamespace().get("header");
            dataType.setText(variable.name());

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
            if (checkCollectVariableFieldsLoaded(false))
                for (int i = 1; i < collectionCollectionBoxVariables.length; i ++)
                    collectionCollectBox.getChildren().add(collectionCollectionBoxVariables[i]);
        });
    }

    private JFXTextField createCollectionCollectBoxTextField(Pane parent, String prompt)
    {
        JFXTextField textField = new JFXTextField();
        textField.setPromptText(prompt);
        textField.prefWidthProperty().bind(parent.prefWidthProperty().multiply(0.92));
        return textField;
    }

    // TODO - FOR DOCUMENTATION ON PARENTS SEE

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
            controller.snackbar(Controller.SnackbarType.ERROR, "Select a Time Series to begin collecting!");
            return;
        }
        if (!checkCollectVariableFieldsLoaded(true)) return;
        if (!checkCollectSetVariables(source)) return;
        if (!checkCollectVariablesValid(source)) return;
        if (!checkCollectQuerySpecified()) return;

        String apiKey = getCollectAPIKey();
        Tuple<Date, Date> queryDates = getCollectQuerySpecified();
        Query query = new Query(queryDates.getB(), queryDates.getA());

        DataType type = source.getDataType();
        CollectionTask task;
        if (type.equals(DataType.DOCUMENT))
            task = createCollectionTask((DataSource<Document>) source, apiKey, query,
                                        (CollectionFilter<Document>) source.newDefaultCollectionFilter());
        else if(type.equals(DataType.STOCK_QUOTE))
            task = createCollectionTask((DataSource<StockQuote>) source, apiKey, query,
                                        (CollectionFilter<StockQuote>) source.newDefaultCollectionFilter());
        else
        {
            controller.snackbar(Controller.SnackbarType.ERROR, "Unknown Collection Data Type" + type.getName());
            return;
        }

        beginCollectionTask(task, series, source);
    }

    private <T extends Data> CollectionTask<T> createCollectionTask(DataSource<T> source, String apiKey, Query query,
                                                                    CollectionFilter<T> collectionFilter)
    {
        return new CollectionTask<>(source, apiKey, source.newSession(query, collectionFilter));
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
                    Class<? extends DataSource<T>> sourceClazz = (Class<? extends DataSource<T>>) source.getClass();
                    // Write the data to the file
                    manager.onDataCollected(series, sourceClazz, data, false);

                    runUIThread(() ->
                    {
                        Framework.info("Collected data, cardinality: " + data.size());
                        // Update the data present tab to reflect changes
                        updateDataPresent(series);
                    });
                }, Constants.COLLECTION_DELAY, Constants.COLLECTION_INTERVAL, TimeUnit.MILLISECONDS);
            }
            catch (TaskAlreadyPresentException e)
            {
                runUIThread(() ->
                {
                    controller.snackbar(Controller.SnackbarType.ERROR,
                                        "Unable to schedule CollectionTask " + e.getMessage());
                });
            }
        });
    }

    public boolean checkCollectVariableFieldsLoaded(boolean checkApi)
    {
        for (int i = (checkApi ? 0 : 1); i < collectionCollectionBoxVariables.length; i ++)
        {
            Parent parent = collectionCollectionBoxVariables[i];
            if (parent == null) return false;
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
        int index = 1;
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

    //////////////////////////////////////////////////////////////////
    //      MISSING / DUPLICATE
    //////////////////////////////////////////////////////////////////

}
