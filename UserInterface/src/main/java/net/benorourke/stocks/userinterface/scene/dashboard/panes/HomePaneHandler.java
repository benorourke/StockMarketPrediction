package net.benorourke.stocks.userinterface.scene.dashboard.panes;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import net.benorourke.stocks.framework.exception.TaskStartException;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.thread.*;
import net.benorourke.stocks.framework.util.Nullable;
import net.benorourke.stocks.userinterface.StockApplication;
import net.benorourke.stocks.userinterface.scene.asyncinflater.InflationResult;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardController;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardModel;
import net.benorourke.stocks.userinterface.util.ResourceUtil;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class HomePaneHandler extends PaneHandler
{
    private JFXButton homeTestButton;

    public HomePaneHandler(DashboardController controller, DashboardModel model, JFXButton homeTestButton)
    {
        super(controller, model);

        this.homeTestButton = homeTestButton;
    }

    @Override
    public void initialise()
    {
        homeTestButton.setOnMouseClicked(e -> {
            StockApplication.runBgThread(framework ->

            {
                try
                {
                    StockApplication.debug("Starting");
                    framework.getTaskManager().scheduleRepeating(new TestTask(), result -> {}, 100, 100, TimeUnit.MILLISECONDS);
                }
                catch (TaskStartException e1)
                {
                    e1.printStackTrace();
                }
            });
        });
    }

    @Override
    public void onTimeSeriesChanged(TimeSeries series)
    {

    }

    public static class TestTask implements Task<TaskDescription, Result>
    {
        public static final TaskType TYPE = () -> "TEST TASK";

        private Progress progress;

        @Override
        public TaskType getType()
        {
            return TYPE;
        }

        @Override
        public TaskDescription getDescription()
        {
            return new TaskDescription(TYPE)
            {
                /**
                 * Shouldn't matter too much since inflation is READ, we can concurrently access resources
                 * @param object
                 * @return
                 */
                @Override
                public boolean equals(Object object) { return false; }
            };
        }

        @Override
        public Progress createTaskProgress()
        {
            return progress = new Progress();
        }

        @Override
        public void run()
        {
            progress.setProgress(Math.min(100, progress.getProgress() + 1));
        }

        @Override
        public boolean isFinished()
        {
            return progress.getProgress() >= 100;
        }

        @Override
        public Result getResult()
        {
            return new Result();
        }
    }


}
