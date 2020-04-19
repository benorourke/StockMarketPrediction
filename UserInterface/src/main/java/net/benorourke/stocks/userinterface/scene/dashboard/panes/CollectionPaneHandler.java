package net.benorourke.stocks.userinterface.scene.dashboard.panes;

import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.userinterface.scene.SceneHelper;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardController;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardModel;

import java.util.Map;

import static net.benorourke.stocks.userinterface.StockApplication.runBgThread;
import static net.benorourke.stocks.userinterface.StockApplication.runUIThread;

public class CollectionPaneHandler extends PaneHandler
{
    private static final String COLLECTION_DATA_FXML = "/dashboard-collection-data.fxml";
    private static final String[] COMBO_OPTIONS = new String[] {"Overview", "Collect Data", "Missing Data"};

    private final JFXComboBox<String> collectionComboBox;
    private final TabPane collectionTabPane;

    // Overview
    private final VBox collectionDataPresentBox;

    // Collect Data

    public CollectionPaneHandler(DashboardController controller, DashboardModel model,
                                 JFXComboBox<String> collectionComboBox, TabPane collectionTabPane,
                                 VBox collectionDataPresentBox)
    {
        super(controller, model);

        this.collectionComboBox = collectionComboBox;
        this.collectionTabPane = collectionTabPane;
        this.collectionDataPresentBox = collectionDataPresentBox;
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
        collectionComboBox.getSelectionModel().select(0);

        // TODO - Do something with
//        model.acquireDataSources(() -> dataSources.setText(String.valueOf(model.getTimeSeries().size())))
        model.acquireDataSources(() -> {});
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
        SceneHelper.inflateAsync(COLLECTION_DATA_FXML, result -> {
            if (!result.isSuccess()) return;

            FXMLLoader loader = result.getLoader();
            Parent parent = result.getLoaded();

            Label dataType = (Label) loader.getNamespace().get("dataType");
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

}
