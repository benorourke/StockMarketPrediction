package net.benorourke.stocks.userinterface.scene.dashboard.panes;

import net.benorourke.stocks.framework.util.Initialisable;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardModel;

public abstract class PaneHandler implements Initialisable
{
    protected DashboardModel model;

    public PaneHandler(DashboardModel model)
    {
        this.model = model;
    }

    public DashboardModel getModel()
    {
        return model;
    }

}
