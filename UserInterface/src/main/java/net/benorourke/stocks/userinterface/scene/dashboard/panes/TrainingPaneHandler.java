package net.benorourke.stocks.userinterface.scene.dashboard.panes;

import com.jfoenix.controls.JFXComboBox;
import javafx.scene.control.Label;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.userinterface.StockApplication;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardModel;

import java.util.stream.Collectors;

public class TrainingPaneHandler extends PaneHandler
{
    private final Label modelHandlers;
    private final JFXComboBox<String> selectHandler;

    public TrainingPaneHandler(DashboardModel model, Label modelHandlers, JFXComboBox<String> selectHandler)
    {
        super(model);

        this.modelHandlers = modelHandlers;
        this.selectHandler = selectHandler;
    }

    @Override
    public void initialise()
    {
        // Load the model handlers & set the text once the value is retrieved
        model.getModelHandlers(() ->
        {
            modelHandlers.setText(String.valueOf(model.getModelHandlerCreators().size()));

            selectHandler.getItems().clear();
            selectHandler.getItems().addAll(model.getModelHandlerCreators()
                                                    .stream().map(c -> c.name())
                                                    .collect(Collectors.toList()));
        });
    }

    @Override
    public void onTimeSeriesChanged(TimeSeries series)
    {
        // TODO
        StockApplication.debug("Verändert");
    }

}
