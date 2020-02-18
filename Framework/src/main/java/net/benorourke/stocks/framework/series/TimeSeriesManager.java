package net.benorourke.stocks.framework.series;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.persistence.FileManager;
import net.benorourke.stocks.framework.persistence.store.DataStore;
import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.stock.Stock;
import net.benorourke.stocks.framework.util.Initialisable;

import java.io.File;
import java.util.*;

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

        Framework.info("Time Series Dir: " + fileManager.getTimeSeriesParentDirectory());
        Framework.info("Time Series Found: " + timeSeries.size());
    }

    //////////////////////////////////////////////////////////////////
    //      TIMESERIES MANAGEMENT
    //////////////////////////////////////////////////////////////////

    public boolean create(String name, Stock stock)
    {
        TimeSeries series = new TimeSeries(name, stock);
        timeSeries.add(series);

        if(save(series))
        {
            Framework.info("Time Series Created: " + series.toString());
            return true;
        }
        else
        {
            Framework.error("Unable to Create TimeSeries " + series.toString());
            return false;
        }
    }

    public boolean save(TimeSeries series)
    {
        File info = fileManager.getTimeSeriesInfoFile(series);
        if(fileManager.writeJson(info, series))
        {
            Framework.info("Time Series Saved: " + series.toString());
            return true;
        }
        else
        {
            Framework.error("Unable to Create TimeSeries " + series.toString());
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

    //////////////////////////////////////////////////////////////////
    //      DATA COLLECTION / PRE-PROCESSING
    //////////////////////////////////////////////////////////////////

    public DataStore getDataStore(TimeSeries timeSeries)
    {
        return new DataStore(framework, timeSeries);
    }

    public void onDataCollected(TimeSeries series, Class<? extends DataSource> dataSourceClass,
                                Collection<Data> data)
    {
        if (getDataStore(series).writeRawData(dataSourceClass, data))
        {
            // Save the TimeSeries with the updated data counts
            series.getRawDataCounts().put(dataSourceClass, data.size());
            save(series);

            framework.info("Wrote raw data to TimeSeries " + series.toString());
        }
        else
        {
            framework.error("Unable to write raw data to TimeSeries " + series.toString());
        }
    }

    public Map<DataSource, Integer> getCollectedDataCounts(TimeSeries series)
    {
        Map<DataSource, Integer> counts = new HashMap<>();
        for (Map.Entry<Class<? extends DataSource>, Integer> entry : series.getRawDataCounts().entrySet())
        {
            DataSource src = framework.getDataSourceManager().getDataSourceByClass(entry.getKey());
            counts.put(src, entry.getValue());
        }
        return counts;
    }

}
