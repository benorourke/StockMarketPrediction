package net.benorourke.stocks.userinterface.scene;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import net.benorourke.stocks.userinterface.exception.InflationException;
import net.benorourke.stocks.userinterface.exception.SceneCreationDataException;
import net.benorourke.stocks.userinterface.util.ResourceUtil;

import java.io.IOException;

public class SceneHelper
{
	
	private SceneHelper() {}

	public static Parent inflate(String fxmlPath) throws InflationException
	{
		FXMLLoader loader = new FXMLLoader(ResourceUtil.getResource(fxmlPath));
		try
		{
			return loader.load();
		}
		catch (IOException e)
		{
			throw new InflationException(e.getMessage());
		}
	}

	public static void modifyStage(Stage stage, String windowTitle,
								   int width, int height,
								   int minWidth, int minHeight,
								   boolean resizable, boolean exitOnClose,
								   SceneType type, Object... params) throws SceneCreationDataException
	{
		stage.setTitle(windowTitle);
		stage.setWidth(width);
		stage.setHeight(height);
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
								   int width, int height,
								   boolean resizable, boolean exitOnClose,
								   SceneType type, Object... params) throws SceneCreationDataException
	{
		stage.setTitle(windowTitle);
		stage.setWidth(width);
		stage.setHeight(height);
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

	/**
	 * Open & show a new stage/window.
	 *
	 * @param windowTitle title of window
	 * @param width 	  width of window
	 * @param height	  height of window
	 * @param minWidth 	  minimum width of window
	 * @param minHeight	  minimum height of window
	 * @param resizable	  whether or not to make the window resizable
	 * @param type	 	  the type of window to show
	 * @param params	  the parameters to pass to the constructor of the controller for this SceneType
	 * @return			  the stage object
	 *
	 * @throws SceneCreationDataException
	 */
	public static Stage openStage(String windowTitle,
								  int width, int height,
								  int minWidth, int minHeight,
								  boolean resizable, boolean exitOnClose,
								  SceneType type, Object... params) throws SceneCreationDataException
	{
		Stage stage = new Stage();
		modifyStage(stage, windowTitle, width, height, minWidth, minHeight, resizable, exitOnClose, type, params);
		return stage;
	}
	
	/**
	 * Open & show a new stage/window.
	 * 
	 * @param windowTitle title of window
	 * @param width 	  width of window
	 * @param height	  height of window
	 * @param resizable	  whether or not to make the window resizable
	 * @param type	 	  the type of window to show
	 * @param params	  the parameters to pass to the constructor of the controller for this SceneType
	 * @return			  the stage object
	 * 
	 * @throws SceneCreationDataException 
	 */
	public static Stage openStage(String windowTitle, 
								  int width, int height,
								  boolean resizable, boolean exitOnClose,
								  SceneType type, Object... params) throws SceneCreationDataException
	{
		Stage stage = new Stage();
		modifyStage(stage, windowTitle, width, height, resizable, exitOnClose, type, params);
        return stage;
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
