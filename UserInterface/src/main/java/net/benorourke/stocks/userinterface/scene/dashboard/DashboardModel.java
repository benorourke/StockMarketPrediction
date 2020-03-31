package net.benorourke.stocks.userinterface.scene.dashboard;

import net.benorourke.stocks.framework.model.ModelHandlerManager;
import net.benorourke.stocks.framework.series.TimeSeries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static net.benorourke.stocks.userinterface.StockApplication.runUIThread;
import static net.benorourke.stocks.userinterface.StockApplication.runBgThread;

public class DashboardModel
{
    private final DashboardController controller;

    // NAVBAR
    private List<TimeSeries> timeSeries;

    // TRAINING
    private List<ModelHandlerManager.RuntimeCreator> modelHandlerCreators;


    protected DashboardModel(DashboardController controller)
    {
        this.controller = controller;
        timeSeries = new ArrayList<>();
        modelHandlerCreators = new ArrayList<>();
    }

    /**
     *
     * @param onLoaded callback is called on the JavaFX UI thread
     */
    public void loadTimeSeries(Runnable onLoaded)
    {
        runBgThread(framework ->
        {
            final List<TimeSeries> clone = Collections.unmodifiableList(
                                                framework.getTimeSeriesManager().getTimeSeries());
            runUIThread(() ->
            {
                timeSeries.clear();
                timeSeries.addAll(clone);
                onLoaded.run();
            });
        });
    }

    public void getModelHandlers(Runnable onRetrieval)
    {
        runBgThread(framework ->
        {
            final List<ModelHandlerManager.RuntimeCreator> creators =
                    Collections.unmodifiableList(framework.getModelHandlerManager().getCreators());

            runUIThread(() ->
            {
                modelHandlerCreators.clear();
                modelHandlerCreators.addAll(creators);
                onRetrieval.run();
            });
        });
    }

    public List<TimeSeries> getTimeSeries()
    {
        return timeSeries;
    }

    public List<ModelHandlerManager.RuntimeCreator> getModelHandlerCreators()
    {
        return modelHandlerCreators;
    }

}
