package net.benorourke.stocks.userinterface.scene.dashboard.panes;

import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardController;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardModel;

public class PreprocessingHandler extends PaneHandler
{

    public PreprocessingHandler(DashboardController controller, DashboardModel model)
    {
        super(controller, model);
    }

    @Override
    public void initialise()
    {

    }

    @Override
    public void onTimeSeriesChanged(TimeSeries series)
    {

    }

}
