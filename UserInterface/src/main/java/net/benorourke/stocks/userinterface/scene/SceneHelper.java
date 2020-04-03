package net.benorourke.stocks.userinterface.scene;

import com.jfoenix.controls.JFXSnackbarLayout;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import net.benorourke.stocks.framework.util.Tuple;
import net.benorourke.stocks.userinterface.exception.InflationException;
import net.benorourke.stocks.userinterface.exception.SceneCreationDataException;
import net.benorourke.stocks.userinterface.util.ResourceUtil;

import java.io.IOException;

public class SceneHelper
{
	
	private SceneHelper() {}

	public static Tuple<FXMLLoader, Parent> inflate(String fxmlPath) throws InflationException
	{
		FXMLLoader loader = new FXMLLoader(ResourceUtil.getResource(fxmlPath));
		try
		{
			return new Tuple<>(loader, loader.load());
		}
		catch (IOException e)
		{
			throw new InflationException(e.getMessage());
		}
	}

	public static void modifyStage(Stage stage, String windowTitle,
								   int minWidth, int minHeight,
								   boolean resizable, boolean exitOnClose,
								   SceneType type, Object... params) throws SceneCreationDataException
	{
		stage.setTitle(windowTitle);
		stage.setMinWidth(minWidth);
		stage.setMinHeight(minHeight);
		stage.setResizable(resizable);
		if (exitOnClose)
			stage.setOnCloseRequest(e ->
			{
				Platform.exit();
				System.exit(0);
			});
		stage.setScene(SceneFactory.getInstance().create(type, params));
		stage.show();
	}

	public static void modifyStage(Stage stage, String windowTitle,
								   boolean resizable, boolean exitOnClose,
								   SceneType type, Object... params) throws SceneCreationDataException
	{
		stage.setTitle(windowTitle);
		stage.setResizable(resizable);
		if (exitOnClose)
			stage.setOnCloseRequest(e ->
			{
				Platform.exit();
				System.exit(0);
			});
		stage.setScene(SceneFactory.getInstance().create(type, params));
		stage.show();
	}

	public static Stage openStage(String windowTitle,
								  boolean resizable, boolean exitOnClose,
								  SceneType type, Object... params) throws SceneCreationDataException
	{
		Stage stage = new Stage();
		modifyStage(stage, windowTitle, resizable, exitOnClose, type, params);
		return stage;
	}

}
