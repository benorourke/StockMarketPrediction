package net.ben.stocks.userinterface;

import javafx.application.Application;
import javafx.stage.Stage;
import net.ben.stocks.userinterface.scene.SceneHelper;
import net.ben.stocks.userinterface.scene.SceneType;
import net.ben.stocks.userinterface.util.Constants;

public class StockApplication extends Application
{

    @Override
    public void start(Stage stage) throws Exception
    {
        SceneHelper.modifyStage(stage, Constants.APPLICATION_NAME, 1280, 720,
                        true, true, SceneType.DIRECTORY_SELECTION);
    }

    public static void main(String[] args)
    {
        launch(args);
    }

}
