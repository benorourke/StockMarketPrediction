package net.benorourke.stocks.userinterface.scene.tasks;

import com.jfoenix.controls.JFXProgressBar;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import net.benorourke.stocks.framework.util.Nullable;

import java.util.UUID;

/*
 * FXML Components are null until they are inflated.
 *
 * The TaskRow is instantiated pre-inflation and the FXML components are injected post-inflation.
 */
public class TaskRow
{
    private UUID taskId;

    /**
     * The component that holds all other components (so we can easily add/remove as children)
     */
    @Nullable
    private Parent parent;
    @Nullable
    private Label text;
    @Nullable
    private Label progressLabel;
    @Nullable
    private JFXProgressBar progressBar;

    public TaskRow(UUID taskId)
    {
        this.taskId = taskId;
    }

    public Parent getParent()
    {
        return parent;
    }

    public void setParent(Parent parent)
    {
        this.parent = parent;
    }

    public Label getText()
    {
        return text;
    }

    public void setText(Label text)
    {
        this.text = text;
    }

    public Label getProgressLabel()
    {
        return progressLabel;
    }

    public void setProgressLabel(Label progressLabel)
    {
        this.progressLabel = progressLabel;
    }

    public JFXProgressBar getProgressBar()
    {
        return progressBar;
    }

    public void setProgressBar(JFXProgressBar progressBar)
    {
        this.progressBar = progressBar;
    }

}
