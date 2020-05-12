package net.benorourke.stocks.userinterface.scene;

import javafx.application.Platform;
import javafx.stage.Stage;
import net.benorourke.stocks.framework.exception.TaskStartException;
import net.benorourke.stocks.framework.thread.ResultCallback;
import net.benorourke.stocks.framework.util.Nullable;
import net.benorourke.stocks.userinterface.StockApplication;
import net.benorourke.stocks.userinterface.exception.SceneCreationDataException;
import net.benorourke.stocks.userinterface.scene.asyncinflater.InflationResult;
import net.benorourke.stocks.userinterface.scene.asyncinflater.InflationTask;

import java.util.concurrent.TimeUnit;

import static net.benorourke.stocks.userinterface.StockApplication.runUIThread;

/**
 * A helper class for creating and managing JavaFX scenes.
 */
public class SceneHelper
{
	private SceneHelper() {}

	/**
	 * FXML inflation takes a while since a file must be read, this will inflate the FXML file.
	 *
	 * Call function from the UI Thread will traverse threads as follows:
	 *
	 * UI THREAD -> FRAMEWORK THREAD -> THREAD POOLS -> FRAMEWORK THREAD -> UI THREAD
	 *
	 * @param fxmlPath
	 * @param uiCallback runs on the UI thread
	 */
	public static void inflateAsync(final String fxmlPath, ResultCallback<InflationResult> uiCallback)
	{
		// CURRENTLY IN UI THREAD
		StockApplication.runBgThread(framework ->
		{
			// CURRENTLY IN FRAMEWORK THREAD
			try
			{
				// ABOUT TO RUN TASK IN POOLED THREAD
				framework.getTaskManager().scheduleRepeating(new InflationTask(fxmlPath), result -> {
					// CURRENTLY IN FRAMEWORK THREAD

					runUIThread(() -> {
						// CURRENTLY IN UI THREAD
						if (!result.isSuccess())
							StockApplication.error("Unable to inflate FXML at " + fxmlPath);

						uiCallback.onCallback(result);

					});

				}, 20, 20, TimeUnit.MILLISECONDS);
			}
			catch (TaskStartException e)
			{
				StockApplication.error("Unable to begin RemoveDuplicatesTask for " + fxmlPath, e);
			}
		});
	}

	/**
	 * A wrapper function to quickly modify the details about a stage & generate a scene inside it.
	 *
	 * @param stage the stage
	 * @param windowTitle the new title
	 * @param minWidth the new minimum width
	 * @param minHeight the new minimum height
	 * @param resizable whether the stage should be resizable
	 * @param exitOnClose whether the stage should force exit the program upon close
	 * @param type the type of stage
	 * @param params the parameters used to instantiate the stage in the factory
	 * @throws SceneCreationDataException an exception thrown if it was unable to create the scene
	 */
	public static void modifyStage(Stage stage, @Nullable String windowTitle,
								   int minWidth, int minHeight,
								   boolean resizable, boolean exitOnClose,
								   SceneType type, Object... params) throws SceneCreationDataException
	{
		if (windowTitle != null)
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

	/**
	 * Pop up a new stage.
	 *
	 * @param windowTitle the new title
	 * @param minWidth the new minimum width
	 * @param minHeight the new minimum height
	 * @param resizable whether the stage should be resizable
	 * @param exitOnClose whether the stage should force exit the program upon close
	 * @param type the type of stage
	 * @param params the parameters used to instantiate the stage in the factory
	 * @return the new popup stage
	 * @throws SceneCreationDataException an exception thrown if it was unable to create the scene
	 */
	public static Stage openStage(String windowTitle, int minWidth, int minHeight,
								  boolean resizable, boolean exitOnClose,
								  SceneType type, Object... params) throws SceneCreationDataException
	{
		Stage stage = new Stage();
		modifyStage(stage, windowTitle, minWidth, minHeight, resizable, exitOnClose, type, params);
		return stage;
	}

}
