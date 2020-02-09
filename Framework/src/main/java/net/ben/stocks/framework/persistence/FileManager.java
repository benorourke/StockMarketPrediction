package net.ben.stocks.framework.persistence;

import com.google.gson.Gson;
import net.ben.stocks.framework.Configuration;
import net.ben.stocks.framework.Framework;
import net.ben.stocks.framework.collection.datasource.DataSource;
import net.ben.stocks.framework.series.TimeSeries;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class FileManager
{
    private final Framework framework;
    private final File workingDirectory;

    public FileManager(Framework framework, Configuration configuration)
    {
        this.framework = framework;
        this.workingDirectory = configuration.getWorkingDirectory();
    }

    public File getWorkingDirectory()
    {
        return workingDirectory;
    }

    //////////////////////////////////////////////////////////////////
    //      GSON
    //////////////////////////////////////////////////////////////////

    public boolean writeJson(File file, Object object)
    {
        if(!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        if(!file.exists())
        {
            try
            {
                file.createNewFile();
            }
            catch (IOException exception)
            {
                return false;
            }
        }

        try (Writer writer = new FileWriter(file))
        {
            framework.getGson().toJson(object, writer);
            writer.close();
            return true;
        }
        catch (IOException error)
        {
            return false;
        }
    }

    //////////////////////////////////////////////////////////////////
    //      TIME SERIES
    //////////////////////////////////////////////////////////////////

    /**
     * Get the parent directory that contains all TimeSeries sub-directories.
     * @return
     */
    public File getTimeSeriesParentDirectory()
    {
        return new File(workingDirectory, "timeseries");
    }

    /**
     * Get the directory for a given TimeSeries' name.
     * @return
     */
    public File getTimeSeriesDirectory(String name)
    {
        return new File(getTimeSeriesParentDirectory(), name.toLowerCase());
    }

    public File getTimeSeriesDirectory(TimeSeries timeSeries)
    {
        return getTimeSeriesDirectory(timeSeries.getName().toLowerCase());
    }

    /**
     * Get the info file for a given TimeSeries.
     *
     * @param seriesDirectory
     * @return
     */
    public File getTimeSeriesInfoFile(File seriesDirectory)
    {
        return new File(seriesDirectory + File.separator + "info.json");
    }

    /**
     * Get the info file for a given TimeSeries.
     *
     * @param timeSeries
     * @return
     */
    public File getTimeSeriesInfoFile(TimeSeries timeSeries)
    {
        return new File(getTimeSeriesDirectory(timeSeries) + File.separator + "info.json");
    }

    public File getDataStoreDirectory(TimeSeries timeSeries)
    {
        return new File(getTimeSeriesDirectory(timeSeries), "store");
    }

    public File getRawDataStoreDirectory(TimeSeries timeSeries)
    {
        return new File(getDataStoreDirectory(timeSeries), "raw");
    }

    public File getRawDataFile(TimeSeries timeSeries, Class<? extends DataSource> clazz)
    {
        return new File(getRawDataStoreDirectory(timeSeries)
                            + File.separator + clazz.getSimpleName().toLowerCase() + ".json");
    }

}
