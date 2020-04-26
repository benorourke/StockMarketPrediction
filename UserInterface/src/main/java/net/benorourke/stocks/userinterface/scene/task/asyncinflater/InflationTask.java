package net.benorourke.stocks.userinterface.scene.task.asyncinflater;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import net.benorourke.stocks.framework.thread.Progress;
import net.benorourke.stocks.framework.thread.Task;
import net.benorourke.stocks.framework.thread.TaskDescription;
import net.benorourke.stocks.framework.thread.TaskType;
import net.benorourke.stocks.framework.util.Nullable;
import net.benorourke.stocks.userinterface.StockApplication;
import net.benorourke.stocks.userinterface.util.ResourceUtil;

import java.io.IOException;

public class InflationTask implements Task<TaskDescription, InflationResult>
{
    public static final TaskType TYPE = () -> "FXML Inflation";

    private final String fxmlPath;

    private Progress progress;

    private boolean finished;
    @Nullable private FXMLLoader loader;
    @Nullable private Parent loaded;

    public InflationTask(String fxmlPath)
    {
        this.fxmlPath = fxmlPath;
        this.finished = false;
    }

    @Override
    public TaskType getType()
    {
        return TYPE;
    }

    @Override
    public TaskDescription getDescription()
    {
        return new TaskDescription(TYPE)
        {
            /**
             * Shouldn't matter too much since inflation is READ, we can concurrently access resources
             * @param object
             * @return
             */
            @Override
            public boolean equals(Object object) { return false; }
        };
    }

    @Override
    public Progress createTaskProgress()
    {
        return progress = new Progress();
    }

    @Override
    public void run()
    {
        StockApplication.info("Inflating FXML at " + fxmlPath);
        loader = new FXMLLoader(ResourceUtil.getResource(fxmlPath));
        try
        {
            loaded = loader.load();
        }
        catch (IOException e) { }

        finished = true;
        progress.setProgress(100);
    }

    @Override
    public boolean isFinished()
    {
        return finished;
    }

    @Override
    public InflationResult getResult()
    {
        return new InflationResult(loaded != null, loader, loaded);
    }
}
