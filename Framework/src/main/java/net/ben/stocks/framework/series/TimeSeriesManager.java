package net.ben.stocks.framework.series;

import net.ben.stocks.framework.Framework;
import net.ben.stocks.framework.persistence.FileManager;
import net.ben.stocks.framework.persistence.store.DataStore;
import net.ben.stocks.framework.stock.Stock;
import net.ben.stocks.framework.util.Initialisable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TimeSeriesManager implements Initialisable
{
    private final Framework framework;
    private final FileManager fileManager;

    private List<TimeSeries> timeSeries;

    public TimeSeriesManager(Framework framework)
    {
        this.framework = framework;
        fileManager = framework.getFileManager();
        timeSeries = new ArrayList<TimeSeries>();
    }

    @Override
    public void initialise()
    {
        timeSeries.addAll(loadStoredTimeSeries());

        Framework.debug("Time Series Dir: " + fileManager.getTimeSeriesParentDirectory());
        Framework.debug("Time Series Found: " + timeSeries.size());
    }

    public boolean create(String name, Stock stock)
    {
        TimeSeries series = new TimeSeries(name, stock);
        File info = fileManager.getTimeSeriesInfoFile(series);

        if(fileManager.writeJson(info, stock))
        {
            Framework.debug("Time Series Created: " + name + " for stock " + stock);
            return true;
        }
        else
        {
            Framework.error("Unable to Create TimeSeries " + name + " for stock " + stock);
            return false;
        }
    }

    public TimeSeries getByName(String name)
    {
        for(TimeSeries series : timeSeries)
        {
            if(series == null) {
                Framework.debug("TimeSeries itself is null");
            } else {
                if(series.getName() == null) {
                    Framework.debug("TimeSeries name is null");
                } else {
                    if(series.getStock() == null) {
                        Framework.debug("TimeSeries stock is null");
                    }
                }
            }
        }

        return timeSeries.stream()
                        .filter(t -> t.getName().equalsIgnoreCase(name))
                        .findFirst()
                        .orElse(null);
    }

    public boolean exists(String name)
    {
        return getByName(name) == null;
    }

    private List<TimeSeries> loadStoredTimeSeries()
    {
        File storageDirectory = fileManager.getTimeSeriesParentDirectory();

        if (!storageDirectory.exists())
            storageDirectory.mkdir();

        List<TimeSeries> result = new ArrayList<TimeSeries>();

        if (storageDirectory.listFiles() == null)
            return result;

        for (File file : storageDirectory.listFiles())
        {
            if (!file.isDirectory()) continue;

            File infoFile = fileManager.getTimeSeriesInfoFile(file);
            try
            {
                TimeSeries loaded = framework.getGson().fromJson(new FileReader(infoFile), TimeSeries.class);
                Framework.debug("Loaded null: " + (loaded == null));
                result.add(loaded);
            }
            catch (FileNotFoundException e)
            {
                framework.error("Unable to load time series ", e);
            }
        }
        return result;
    }

    public DataStore getDataStore(TimeSeries timeSeries)
    {
        return new DataStore(framework, timeSeries);
    }

}
