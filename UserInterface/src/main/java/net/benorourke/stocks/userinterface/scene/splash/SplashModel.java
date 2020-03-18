package net.benorourke.stocks.userinterface.scene.splash;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.userinterface.BackgroundRunnable;

import java.util.ArrayList;
import java.util.List;

import static net.benorourke.stocks.userinterface.StockApplication.runUIThread;
import static net.benorourke.stocks.userinterface.StockApplication.runBackgroundThread;

public class SplashModel
{
	private final SplashController controller;
	
	public SplashModel(SplashController splashController)
    {
    	this.controller = splashController;
	}

	public void loadTimeSeries()
	{
		runBackgroundThread(new BackgroundRunnable()
		{

			@Override
			public void run(Framework framework)
			{
				List<TimeSeries> clone = new ArrayList<>(framework.getTimeSeriesManager().getTimeSeries());
			}

		});
	}
	
}
