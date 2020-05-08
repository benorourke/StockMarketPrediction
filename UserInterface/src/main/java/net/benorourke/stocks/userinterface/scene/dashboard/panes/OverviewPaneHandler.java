package net.benorourke.stocks.userinterface.scene.dashboard.panes;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.collection.datasource.DataSourceManager;
import net.benorourke.stocks.framework.persistence.gson.ParameterizedTypes;
import net.benorourke.stocks.framework.persistence.store.DataStore;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.series.TimeSeriesManager;
import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.IdentifiableData;
import net.benorourke.stocks.framework.series.data.impl.Document;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;
import net.benorourke.stocks.framework.series.data.impl.StockQuoteDataType;
import net.benorourke.stocks.framework.util.DateUtil;
import net.benorourke.stocks.framework.util.Nullable;
import net.benorourke.stocks.framework.util.StringUtil;
import net.benorourke.stocks.userinterface.scene.Controller;
import net.benorourke.stocks.userinterface.scene.SceneHelper;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardController;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardModel;
import net.benorourke.stocks.userinterface.scene.dashboard.FlowStage;
import net.benorourke.stocks.userinterface.util.JavaFXUtil;

import java.lang.reflect.ParameterizedType;
import java.util.*;

import static net.benorourke.stocks.userinterface.StockApplication.runBgThread;
import static net.benorourke.stocks.userinterface.StockApplication.runUIThread;
import static net.benorourke.stocks.userinterface.scene.dashboard.DashboardController.*;

public class OverviewPaneHandler extends PaneHandler
{
    private static final String[] COMBO_OPTIONS = new String[] {"Overview", "View Data", "Danger Zone"};

    private final JFXComboBox<String> overviewComboBox;
    private final TabPane overviewTabPane;

    // Overview
    private final VBox overviewDataPresentBox;
    private final PieChart overviewDistributionChart;

    // View Data
    private final JFXComboBox<String> overviewViewSources;
    private final VBox overviewEntriesListViewBox;
    /** Dynamically created rather than using FXML since we want to use Data as the type. */
    private ListView<Data> overviewEntriesListView;
    private final VBox overviewEntryViewerBox;
    private final JFXButton overviewEntryRemove;

    // Danger Zone
    private final JFXButton overviewDuplicatesRemoveButton;
    private final JFXButton overviewDeleteButton;

    public OverviewPaneHandler(DashboardController controller, DashboardModel model,
                               JFXComboBox<String> overviewComboBox, TabPane overviewTabPane,
                               VBox overviewDataPresentBox, PieChart overviewDistributionChart,
                               JFXComboBox<String> overviewViewSources, VBox overviewEntriesListViewBox,
                               VBox overviewEntryViewerBox, JFXButton overviewEntryRemove,
                               JFXButton overviewDuplicatesRemoveButton, JFXButton overviewDeleteButton)
    {
        super(controller, model);

        this.overviewComboBox = overviewComboBox;
        this.overviewTabPane = overviewTabPane;

        this.overviewDataPresentBox = overviewDataPresentBox;
        this.overviewDistributionChart = overviewDistributionChart;

        this.overviewViewSources = overviewViewSources;
        this.overviewEntriesListViewBox = overviewEntriesListViewBox;
        this.overviewEntryViewerBox = overviewEntryViewerBox;
        this.overviewEntryRemove = overviewEntryRemove;

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

        // VIEW DATA
        overviewViewSources.valueProperty().addListener(
                (observable, oldValue, newValue) -> onViewDataSourceSelected(newValue));

        overviewEntriesListView = new ListView<>();
        overviewEntriesListViewBox.getChildren().add(overviewEntriesListView);
        overviewEntriesListView.setEditable(false);
        overviewEntriesListView.setCellFactory(createViewDataFactory());
        overviewEntriesListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> onViewDataElementSelected(newValue));
        overviewEntryRemove.setVisible(false);
        overviewEntryRemove.setOnMouseClicked(event -> onRemoveEntryClicked());

        // DANGER ZONE
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

    @Override
    public void onSwitchedTo() { }

    //////////////////////////////////////////////////////////////////
    //      GENERAL
    //////////////////////////////////////////////////////////////////

    public void updateDataPresent(TimeSeries series)
    {
        overviewDataPresentBox.getChildren().clear();
        overviewDistributionChart.getData().clear();

        overviewViewSources.getItems().clear();
        overviewEntriesListView.getItems().clear();

        for (Map.Entry<Class<? extends DataSource>, Integer> entry : series.getRawDataCounts().entrySet())
        {
            final Class<? extends DataSource> clazz = entry.getKey();
            final int count = entry.getValue();
            runBgThread(framework ->
            {
                final DataSource src = framework.getDataSourceManager().getDataSourceByClass(clazz);
                if (src == null) return;

                runUIThread(() ->
                {
                    // Populate the present data
                    inflateCollectionDataSync(src, count);
                    addViewDataSources(src);
                });
            });
        }
    }

    //////////////////////////////////////////////////////////////////
    //      OVERVIEW
    //////////////////////////////////////////////////////////////////

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
    //      VIEW DATA
    //////////////////////////////////////////////////////////////////

    public void addViewDataSources(DataSource source)
    {
        overviewViewSources.getItems().add(source.getName());

        if (overviewViewSources.getItems().size() == 1
                && !overviewViewSources.getSelectionModel().isSelected(0))
            overviewViewSources.getSelectionModel().select(0);
    }

    public void onViewDataSourceSelected(final String name)
    {
        if (name == null) return;

        final TimeSeries series = model.getCurrentlySelectedTimeSeries();
        if (series == null) return; // this should never happen, but in case it does

        runBgThread(framework ->
        {
            DataStore store = framework.getTimeSeriesManager().getDataStore(series);
            DataSource source = framework.getDataSourceManager().getDataSourceByName(name);

            if (source.getDataType().equals(DataType.DOCUMENT))
            {
                DataSource<Document> castedSource = (DataSource<Document>) source;
                runUIThread(() -> populateViewDataEntries(store.loadRawDocuments(castedSource)));
            }
            else if (source.getDataType().equals(DataType.STOCK_QUOTE))
            {
                DataSource<StockQuote> castedSource = (DataSource<StockQuote>) source;
                runUIThread(() -> populateViewDataEntries(store.loadRawStockQuotes(castedSource)));
            }
        });
    }

    private <T extends Data> void populateViewDataEntries(List<T> data)
    {
        ObservableList<Data> items = FXCollections.observableArrayList(data);
        overviewEntriesListView.setItems(items);
    }

    private Callback<ListView<Data>, ListCell<Data>> createViewDataFactory()
    {
        return new Callback<ListView<Data>, ListCell<Data>>()
        {

            @Override
            public ListCell<Data> call(ListView<Data> param)
            {
                return new ListCell<Data>()
                {
                    @Override
                    public void updateItem(Data item, boolean empty)
                    {
                        super.updateItem(item, empty);

                        if (item == null)
                        {
                            setText(null);
                            setTooltip(null);
                        }
                        else
                        {
                            setText(DateUtil.formatSimple(item.getDate()));
                            if (item instanceof IdentifiableData)
                                setTooltip(new Tooltip( "ID: " + ((IdentifiableData) item).getId().toString() ));
                        }
                    }
                };
            }
        };
    }

    public void onViewDataElementSelected(@Nullable Data data)
    {
        overviewEntryRemove.setVisible(false);
        overviewEntryViewerBox.getChildren().clear();
        if (data == null) return;

        LinkedHashMap<String, String> toDisplay = new LinkedHashMap<>();
        toDisplay.put("Date", DateUtil.formatDetailed(data.getDate()));
        if (data instanceof StockQuote)
        {
            StockQuote quote = (StockQuote) data;
            for (StockQuoteDataType type : StockQuoteDataType.values())
                toDisplay.put(type.getLocale(), StringUtil.formatDouble(quote.toVector()[type.index()]));
        }
        else if (data instanceof Document)
        {
            Document document = (Document) data;
            toDisplay.put("Document Type", document.getDocumentType().toString());
            toDisplay.put("Content", document.getContent());
        }

        inflateEntry(toDisplay);
    }

    private void inflateEntry(LinkedHashMap<String, String> toDisplay)
    {
        final Parent[] parents = new Parent[toDisplay.size()];
        int index = 0;
        for (final Map.Entry<String, String> entry : toDisplay.entrySet())
        {
            final int finalIndex = index ++;
            SceneHelper.inflateAsync(TEXT_FIELD_FXML, result ->
            {
                if (!result.isSuccess()) return;

                FXMLLoader loader = result.getLoader();
                Parent parent = result.getLoaded();

                Label label = (Label) loader.getNamespace().get("header");
                label.setText(entry.getKey());

                Label content = (Label) loader.getNamespace().get("content");
                content.setText(entry.getValue());
                content.setTooltip(new Tooltip(entry.getValue()));

                parents[finalIndex] = parent;
                boolean foundNull = false;
                for (Parent elem : parents)
                    if (elem == null) foundNull = true;

                // If none of the parents are null, this means all the parents to inflate have been inflated; now we
                // can add the parents into the box as children
                if (!foundNull)
                {
                    overviewEntryRemove.setVisible(true);

                    for (Parent elem : parents)
                        overviewEntryViewerBox.getChildren().add(elem);
                }
            });
        }
    }

    public void onRemoveEntryClicked()
    {
        TimeSeries series = model.getCurrentlySelectedTimeSeries();
        if (series == null)
        {
            controller.snackbarNullTimeSeries();
            return;
        }

        final Data selected = overviewEntriesListView.getSelectionModel().getSelectedItem();
        if (selected == null) return; // This shouldn't happen, but in case it does

        final ParameterizedType type;
        if (selected instanceof StockQuote)
            type = ParameterizedTypes.LIST_STOCKQUOTE;
        else if (selected instanceof Document)
            type = ParameterizedTypes.LIST_DOCUMENT;
        else
        {
            controller.snackbar(SnackbarType.ERROR,
                        "Unable to resolve ParameterizedType for " + selected.getClass().getSimpleName());
            return;
        }

        final UUID id = ((IdentifiableData) selected).getId();
        // For this button to be present; they must've selected an entry which means there is sure to be a source
        String dataSourceName = overviewViewSources.getSelectionModel().getSelectedItem();

        runBgThread(framework ->
        {
            DataSource source = framework.getDataSourceManager().getDataSourceByName(dataSourceName);
            DataStore store = framework.getTimeSeriesManager().getDataStore(series);

            int count = store.removeRawData(source, type, id);
            if (count != 0) framework.getTimeSeriesManager().onDataRemoved(series, source, count);

            runUIThread(() ->
            {
                if (count > 0)
                {
                    onViewDataElementSelected(null);
                    overviewEntriesListView.getItems().remove(selected);
                    controller.snackbar(SnackbarType.INFO, "Successfully Removed");
                    updateDataPresent(series);
                }
                else
                    controller.snackbar(SnackbarType.ERROR, "Unable to remove data. Please try again.");
            });
        });
    }

    //////////////////////////////////////////////////////////////////
    //      DANGER ZONE
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
