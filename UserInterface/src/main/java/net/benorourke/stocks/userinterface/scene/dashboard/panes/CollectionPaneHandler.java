package net.benorourke.stocks.userinterface.scene.dashboard.panes;

import com.jfoenix.controls.JFXComboBox;
import javafx.scene.control.TabPane;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardController;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardModel;

public class CollectionPaneHandler extends PaneHandler
{
    private static final String[] COMBO_OPTIONS = new String[] {"Overview", "Collect Data", "Missing Data"};

    private final JFXComboBox<String> collectionComboBox;
    private final TabPane collectionTabPane;

    public CollectionPaneHandler(DashboardController controller, DashboardModel model,
                                 JFXComboBox<String> collectionComboBox, TabPane collectionTabPane)
    {
        super(controller, model);

        this.collectionComboBox = collectionComboBox;
        this.collectionTabPane = collectionTabPane;
    }

    @Override
    public void initialise()
    {
        collectionComboBox.getItems().addAll(COMBO_OPTIONS);
        collectionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
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
    }

    @Override
    public void onTimeSeriesChanged(TimeSeries series)
    {

    }

}
