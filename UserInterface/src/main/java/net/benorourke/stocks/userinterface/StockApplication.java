package net.benorourke.stocks.userinterface;

import com.google.gson.Gson;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import net.benorourke.stocks.framework.Configuration;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.collection.datasource.DataSourceManager;
import net.benorourke.stocks.framework.persistence.FileManager;
import net.benorourke.stocks.framework.series.TimeSeriesManager;
import net.benorourke.stocks.framework.stock.StockExchangeManager;
import net.benorourke.stocks.framework.thread.TaskManager;
import net.benorourke.stocks.userinterface.scene.SceneHelper;
import net.benorourke.stocks.userinterface.scene.SceneType;
import net.benorourke.stocks.userinterface.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StockApplication extends Application
{
    private static final long BACKGROUND_SLEEP_DELAY = 100;
    private static final Logger LOGGER;

    static
    {
        LOGGER = LoggerFactory.getLogger(StockApplication.class);
    }

    private static BackgroundThread backgroundThread;

    public StockApplication()
    {
        backgroundThread = new BackgroundThread(new Configuration(), BACKGROUND_SLEEP_DELAY);
        backgroundThread.setName("Background Thread");
        backgroundThread.start();
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        SceneHelper.modifyStage(stage, Constants.APPLICATION_NAME,
                                Constants.APPLICATION_WIDTH, Constants.APPLICATION_HEIGHT,
                                Constants.APPLICATION_WIDTH_MIN, Constants.APPLICATION_HEIGHT_MIN,
                        true, true, SceneType.DASHBOARD);
    }

    public static void main(String[] args)
    {
        launch(args);
    }

    public static void runUIThread(Runnable runnable)
    {
        Platform.runLater(runnable);
    }

    public static void runBgThread(BackgroundRunnable runnable)
    {
        // TODO - set as Daemon Thread (low prior, background thread)?
        new Thread(() -> backgroundThread.queueRunnable(runnable)).start();
    }

    public static void info(String message)
{
    LOGGER.info(message);
}

    public static void debug(String message)
    {
        LOGGER.info("Debug: " + message);
    }

    public static void error(String message)
    {
        LOGGER.error(message);
    }

    public static void error(String message, Throwable throwable)
    {
        LOGGER.error(message, throwable);
    }

}
