package net.benorourke.stocks.userinterface.scene.dashboard;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import net.benorourke.stocks.framework.thread.Progress;
import net.benorourke.stocks.framework.thread.TaskDescription;
import net.benorourke.stocks.framework.util.Initialisable;
import net.benorourke.stocks.userinterface.StockApplication;
import net.benorourke.stocks.userinterface.TaskUpdateAdapter;
import net.benorourke.stocks.userinterface.scene.tasks.TasksController;
import net.benorourke.stocks.userinterface.util.Constants;

import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

public class BottomBarHelper implements Initialisable, TaskUpdateAdapter
{
    /** Parent of the tasks running spinner & label. */
    @FXML private HBox tasksRunningBox;
    @FXML private FontAwesomeIcon tasksRunningSpinner;
    @FXML private Label tasksRunningLabel;
    @FXML private FontAwesomeIcon shutdownIcon;

    public BottomBarHelper(HBox tasksRunningBox, FontAwesomeIcon tasksRunningSpinner, Label tasksRunningLabel,
                           FontAwesomeIcon shutdownIcon)
    {
        this.tasksRunningBox = tasksRunningBox;
        this.tasksRunningSpinner = tasksRunningSpinner;
        this.tasksRunningLabel = tasksRunningLabel;
        this.shutdownIcon = shutdownIcon;
    }

    @Override
    public void initialise()
    {
        tasksRunningBox.setOnMouseClicked(event -> TasksController.show());

        tasksRunningSpinner.setVisible(false);
        // Create a rotation to constantly rotate the spinner
        RotateTransition spinnerTransition = new RotateTransition(Duration.seconds(2), tasksRunningSpinner);
        spinnerTransition.setFromAngle(0);
        spinnerTransition.setToAngle(360);
        spinnerTransition.setInterpolator(Interpolator.LINEAR);
        spinnerTransition.setCycleCount(Animation.INDEFINITE);
        spinnerTransition.play();

        StockApplication.registerTaskAdapter(this);
        shutdownIcon.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> System.exit(0));
    }

    @Override
    public void update(Map<UUID, TaskDescription> descriptions, Map<UUID, Progress> progresses)
    {
        // Filter out any task types that we chose to ignore (i.e. inflation)
        Predicate<Map.Entry<UUID, TaskDescription>> filter =
                e -> !Constants.TASKS_TO_IGNORE.contains(e.getValue().getType());
        final int count = (int) descriptions.entrySet().stream()
                                                       .filter(filter)
                                                       .count();
        final String text = count + (count == 1 ? " Task" : " Tasks").concat(" Running");
        StockApplication.runUIThread(() ->
        {
            tasksRunningLabel.setText(text);

            if (count > 0)
                tasksRunningSpinner.setVisible(true);
            else
                tasksRunningSpinner.setVisible(false);
        });
    }

}
