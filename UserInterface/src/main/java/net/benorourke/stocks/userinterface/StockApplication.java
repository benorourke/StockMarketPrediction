package net.benorourke.stocks.userinterface;

import javafx.application.Application;
import javafx.stage.Stage;
import net.benorourke.stocks.userinterface.scene.SceneHelper;
import net.benorourke.stocks.userinterface.scene.SceneType;
import net.benorourke.stocks.userinterface.util.Constants;

public class StockApplication extends Application
{

    @Override
    public void start(Stage stage) throws Exception
    {
        SceneHelper.modifyStage(stage, Constants.APPLICATION_NAME,
                                Constants.APPLICATION_WIDTH, Constants.APPLICATION_HEIGHT,
                        true, true,
                                SceneType.DIRECTORY_SELECTION);
    }

    public static void main(String[] args)
    {
        launch(args);
    }

}
