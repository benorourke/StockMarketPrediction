package net.ben.stocks.framework.series;

import net.ben.stocks.framework.Framework;
import net.ben.stocks.framework.persistence.gson.FileManager;
import net.ben.stocks.framework.stock.Stock;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TimeSeriesManager
{
    private final Framework framework;
    private final FileManager fileManager;

    public TimeSeriesManager(Framework framework)
    {
        this.framework = framework;
        fileManager = framework.getFileManager();

        Framework.debug("Time Series Dir: " + fileManager.getTimeSeriesParentDirectory());
        Framework.debug("Time Series Found: " + getStoredTimeSeries().size());
    }

    public boolean create(String name, Stock stock)
    {
        TimeSeries series = new TimeSeries(name, stock);
        File info = fileManager.getTimeSeriesInfoFile(fileManager.getTimeSeriesDirectory(name));

        if(!info.getParentFile().exists())
            info.getParentFile().mkdirs();
        if(!info.exists())
        {
            try
            {
                info.createNewFile();
            }
            catch (IOException exception)
            {
                Framework.error("Unable to Create TimeSeries Info File", exception);
                return false;
            }
        }

        try (Writer writer = new FileWriter(info))
        {
            framework.getGson().toJson(stock, writer);
            Framework.debug("Time Series Created: " + name + " for stock " + stock);
            writer.close();
            return true;
        }
        catch (IOException error)
        {
            Framework.error("Unable to Create TimeSeries " + name + " for stock " + stock, error);
            return false;
        }
    }

    public TimeSeries getByName(String name)
    {
        return getStoredTimeSeries().stream()
                        .filter(t -> t.getName().equalsIgnoreCase(name))
                        .findFirst()
                        .orElse(null);
    }

    public boolean exists(String name)
    {
        return getStoredTimeSeries().stream()
                        .anyMatch(t -> t.getName().equalsIgnoreCase(name));
    }

    private List<TimeSeries> getStoredTimeSeries()
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
                result.add(framework.getGson().fromJson(new FileReader(infoFile), TimeSeries.class));
            }
            catch (FileNotFoundException e)
            {
                framework.error("Unable to load time series ", e);
            }
        }
        return result;
    }

}
