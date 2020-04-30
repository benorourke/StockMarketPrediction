package net.benorourke.stocks.userinterface.scene.dashboard.panes;

import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.util.Initialisable;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardController;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardModel;

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
     * Whether the user can switch to this pane for a given TimeSeries.
     *
     * For example, users should not be able to switch to the evaluation pane before they have trained any models.
     *
     * @return
     */
//    public abstract boolean canSwitch();

    public DashboardController getController()
    {
        return controller;
    }

    public DashboardModel getModel()
    {
        return model;
    }

}
