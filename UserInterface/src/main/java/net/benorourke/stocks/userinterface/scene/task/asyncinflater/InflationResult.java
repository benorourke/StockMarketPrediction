package net.benorourke.stocks.userinterface.scene.task.asyncinflater;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import net.benorourke.stocks.framework.thread.Result;

public class InflationResult extends Result
{
    private final boolean success;
    private final FXMLLoader loader;
    private final Parent loaded;

    public InflationResult(boolean success, FXMLLoader loader, Parent loaded)
    {
        this.success = success;
        this.loader = loader;
        this.loaded = loaded;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public FXMLLoader getLoader()
    {
        return loader;
    }

    public Parent getLoaded()
    {
        return loaded;
    }

}
