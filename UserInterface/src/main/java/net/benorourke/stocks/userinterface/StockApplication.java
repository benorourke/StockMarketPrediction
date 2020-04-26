package net.benorourke.stocks.userinterface;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.benorourke.stocks.framework.Configuration;
import net.benorourke.stocks.userinterface.scene.SceneHelper;
import net.benorourke.stocks.userinterface.scene.SceneType;
import net.benorourke.stocks.userinterface.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StockApplication extends Application
{
    private static final long BACKGROUND_SLEEP_DELAY = 100;
    private static final Logger LOGGER  = LoggerFactory.getLogger(StockApplication.class);

    private static BackgroundThread backgroundThread;

    public StockApplication()
    {
        Configuration config = new Configuration();
        config.setTaskPoolSize(Constants.TASK_POOL_SIZE);

        backgroundThread = new BackgroundThread(config, BACKGROUND_SLEEP_DELAY);
        backgroundThread.setName("Background Thread");
        backgroundThread.start();
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        stage.initStyle(StageStyle.UNDECORATED);
        SceneHelper.modifyStage(stage, Constants.APPLICATION_NAME,
                                Constants.APPLICATION_WIDTH_MIN, Constants.APPLICATION_HEIGHT_MIN,
                        false, false, true, SceneType.DASHBOARD);
        stage.requestFocus();
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
