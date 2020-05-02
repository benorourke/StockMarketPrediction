package net.benorourke.stocks.userinterface.scene.tasks;

import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.benorourke.stocks.framework.thread.Progress;
import net.benorourke.stocks.framework.thread.TaskDescription;
import net.benorourke.stocks.framework.util.Nullable;
import net.benorourke.stocks.userinterface.StockApplication;
import net.benorourke.stocks.userinterface.TaskUpdateAdapter;
import net.benorourke.stocks.userinterface.exception.SceneCreationDataException;
import net.benorourke.stocks.userinterface.scene.Controller;
import net.benorourke.stocks.userinterface.scene.SceneHelper;
import net.benorourke.stocks.userinterface.scene.SceneType;
import net.benorourke.stocks.userinterface.util.Constants;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TasksController extends Controller implements TaskUpdateAdapter
{
    private static final String TASKS_ROW_FXML = "/tasks-row.fxml";
    private static final DecimalFormat PERCENTAGE_FORMAT = new DecimalFormat("##");

    /**
     * Only one TasksController instance can be open at a time
     */
    @Nullable
    private static Stage singletonInstance;

    @FXML
    private VBox parentBox;

    private final Map<UUID, TaskRow> rows;

    public TasksController()
    {
        this.rows = new LinkedHashMap<>();
    }

    @FXML
    public void initialize()
    {
        StockApplication.registerTaskAdapter(this);
        Platform.runLater(() ->
        {
            parentBox.getScene().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, e ->
            {
                StockApplication.unregisterTaskAdapter(this);
                singletonInstance = null;
            });
        });
    }

    @Override
    public void update(Map<UUID, TaskDescription> descriptions, Map<UUID, Progress> progresses)
    {
        // Removed ignored types (i.e. Inflation)
        Predicate<Map.Entry<UUID, TaskDescription>> filter =
                e -> Constants.TASKS_TO_IGNORE.contains(e.getValue().getType());
        Set<UUID> toRemove = new HashSet<>(descriptions.entrySet().stream()
                                                                  .filter(filter).map(e -> e.getKey())
                                                                  .collect(Collectors.toList()));
        for (UUID id : toRemove)
        {
            descriptions.remove(id);
            progresses.remove(id);
        }

        StockApplication.runUIThread(() ->
        {
            // The set of all current (ongoing, or queued) tasks
            Set<UUID> ids = descriptions.keySet();

            // The set of tasks that have just started and we don't have a row for
            Set<UUID> started = ids.stream()
                                   .filter(id -> !rows.containsKey(id))
                                   .collect(Collectors.toSet());
            // The set of tasks we have a row for, but no longer need as the task is finished (and is no longer considered
            // an ongoing task)
            Set<UUID> completed = rows.keySet().stream()
                                               .filter(id -> !ids.contains(id))
                                               .collect(Collectors.toSet());
            // The set of tasks that we have a row for, but only need updating
            Set<UUID> existing = ids.stream()
                                    .filter(id -> !(started.contains(id) || completed.contains(id)))
                                    .collect(Collectors.toSet());

            updateExisting(existing, descriptions, progresses);
            addStarted(started, descriptions, progresses);
            removeCompleted(completed);
        });
    }

    private void setProgress(TaskRow row, double progress)
    {
        if (progress == 0.0)
            row.getProgressBar().setProgress(JFXProgressBar.INDETERMINATE_PROGRESS);
        else
            row.getProgressBar().setProgress(progress / 100D);

        row.getProgressLabel().setText(PERCENTAGE_FORMAT.format(progress).concat("%"));
    }

    public void updateExisting(Set<UUID> ids, Map<UUID, TaskDescription> descriptions, Map<UUID, Progress> progresses)
    {
        for (UUID id : ids)
        {
            TaskRow row = rows.get(id);
            if (row.getParent() == null) // It might not be inflated yet, we'll ignore it and wait if it's not
                continue;

            setProgress(row, progresses.get(id).getProgress());
        }
    }

    public void addStarted(Set<UUID> ids, Map<UUID, TaskDescription> descriptions, Map<UUID, Progress> progresses)
    {
        for (final UUID id : ids)
        {
            final TaskRow row = new TaskRow(id);
            rows.put(id, row);

            final String taskName = descriptions.get(id).getType().getName();
            final double progress = progresses.get(id).getProgress();
            SceneHelper.inflateAsync(TASKS_ROW_FXML, result -> {
                if (!result.isSuccess()) return;

                FXMLLoader loader = result.getLoader();
                Parent parent = result.getLoaded();

                Label text = (Label) loader.getNamespace().get("text");
                Label progressLabel = (Label) loader.getNamespace().get("progressLabel");
                JFXProgressBar progressBar = (JFXProgressBar) loader.getNamespace().get("progressBar");
                VBox cancelParent = (VBox) loader.getNamespace().get("cancelParent");

                row.setParent(parent);
                row.setText(text);
                row.setProgressLabel(progressLabel);
                row.setProgressBar(progressBar);
                row.setCancelParent(cancelParent);

                text.setText(taskName);
                setProgress(row, progress);
                cancelParent.setOnMouseClicked(e -> StockApplication.runBgThread(
                                                            framework -> framework.getTaskManager().cancel(id)));

                parentBox.getChildren().add(parent);
            });
        }
    }

    public void removeCompleted(Set<UUID> ids)
    {
        for (UUID id : ids)
        {
            TaskRow row = rows.get(id);
            parentBox.getChildren().remove(row.getParent());
            rows.remove(id);
        }
    }

    public static void show()
    {
        if (singletonInstance == null)
        {
            try
            {
                singletonInstance = SceneHelper.openStage(Constants.TASKS_NAME,
                        Constants.TASKS_WIDTH_MIN, Constants.TASKS_HEIGHT_MIN,
                        false, false, SceneType.TASKS);
            }
            catch (SceneCreationDataException e) { return; }
        }

        singletonInstance.requestFocus();
    }

}
