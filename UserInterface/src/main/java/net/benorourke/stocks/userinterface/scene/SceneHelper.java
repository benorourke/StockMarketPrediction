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
			// TODO: Throw inflation exception on some thread if unsuccessful
		});
	}

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

	public static Stage openStage(String windowTitle, int minWidth, int minHeight,
								  boolean resizable, boolean exitOnClose,
								  SceneType type, Object... params) throws SceneCreationDataException
	{
		Stage stage = new Stage();
		modifyStage(stage, windowTitle, minWidth, minHeight, resizable, exitOnClose, type, params);
		return stage;
	}

}
