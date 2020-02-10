package net.ben.stocks.framework.series;

import net.ben.stocks.framework.Framework;
import net.ben.stocks.framework.persistence.FileManager;
import net.ben.stocks.framework.persistence.store.DataStore;
import net.ben.stocks.framework.stock.Stock;
import net.ben.stocks.framework.util.Initialisable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        timeSeries.add(series);

        if(fileManager.writeJson(info, series))
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
        return timeSeries.stream()
                        .filter(t -> t.getName().equalsIgnoreCase(name))
                        .findFirst()
                        .orElse(null);
    }

    public boolean exists(String name)
    {
        return getByName(name) != null;
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

            if(!infoFile.exists()) // There's a TimeSeries directory but the info file is missing
            {
                Framework.info("Timeseries info.json " + infoFile.getPath() + " is missing! Unable to load");
                continue;
            }

            Optional<TimeSeries> timeSeries = fileManager.loadJson(infoFile, TimeSeries.class);
            if(timeSeries != null)
            {
                result.add(timeSeries.get());
            }
            else
            {
                framework.error("Unable to load time series meta at " + infoFile.getPath());
            }
        }
        return result;
    }

    public DataStore getDataStore(TimeSeries timeSeries)
    {
        return new DataStore(framework, timeSeries);
    }

}
