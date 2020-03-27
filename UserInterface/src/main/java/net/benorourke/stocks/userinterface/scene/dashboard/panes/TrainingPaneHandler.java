package net.benorourke.stocks.userinterface.scene.dashboard.panes;

import com.jfoenix.controls.JFXComboBox;
import javafx.scene.control.Label;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.userinterface.BackgroundRunnable;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardModel;

import static net.benorourke.stocks.userinterface.StockApplication.runBgThread;
import static net.benorourke.stocks.userinterface.StockApplication.runUIThread;

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
        runBgThread(framework -> {
            List<String> model
            runUIThread();
        });
    }

}
