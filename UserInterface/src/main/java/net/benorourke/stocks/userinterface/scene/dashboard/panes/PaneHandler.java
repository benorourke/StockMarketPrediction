package net.benorourke.stocks.userinterface.scene.dashboard.panes;

import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.util.Initialisable;
import net.benorourke.stocks.framework.util.Nullable;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardController;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardModel;
import net.benorourke.stocks.userinterface.scene.dashboard.FlowStage;

public abstract class PaneHandler implements Initialisable
{
    protected DashboardController controller;
    protected DashboardModel model;

    public PaneHandler(DashboardController controller, DashboardModel model)
    {
        this.controller = controller;
        this.model = model;
    }

    public abstract void onTimeSeriesChanged(TimeSeries series);

    /**
     * The minimum FlowStage that is required to navigate to this pane.
     *
     * @return null if no requirement (i.e. overview, collection, injection)
     */
    @Nullable
    public abstract FlowStage getNavigationRequirement();

    public abstract void onSwitchedTo();

    public DashboardController getController()
    {
        return controller;
    }

    public DashboardModel getModel()
    {
        return model;
    }

}
