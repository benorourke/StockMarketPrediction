package net.benorourke.stocks.framework.persistence.store;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.persistence.FileManager;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.series.data.Data;

import java.io.File;
import java.util.*;

public class DataStore
{
    private final Framework framework;
    private final FileManager fileManager;
    private final TimeSeries timeSeries;

    public DataStore(Framework framework, TimeSeries timeSeries)
    {
        this.framework = framework;
        this.fileManager = framework.getFileManager();
        this.timeSeries = timeSeries;
    }

    //////////////////////////////////////////////////////////////////
    //      RAW DATA
    //////////////////////////////////////////////////////////////////

    // Example directory:
    //
    // - TimeSeriesA
    //    - raw
    //        - newsapi.json
    //        - alphavantage.json
    //    - processed
    //        - processeddocuments.json
    //        - processedstockquotes.json

    public boolean rawDataExists(Class<? extends DataSource> source)
    {
        return fileManager.getRawDataFile(timeSeries, source).exists();
    }

    public <T extends Data> boolean writeRawData(Class<? extends DataSource> source,
                                                 Collection<T> data)
    {
        File file = fileManager.getRawDataFile(timeSeries, source);
        file.getParentFile().mkdirs();

        if(!file.exists()) file.delete(); // TODO- Maybe append?

        if(fileManager.writeJson(file, data))
        {
            Framework.debug("Wrote " + data.size() + ": raw data to " + file.toString());
            return true;
        }
        else
        {
            Framework.error("Unable to write raw data to " + file.toString());
            return false;
        }
    }

    public <T extends Data> List<T> loadRawData(Class<? extends DataSource<T>> source,
                                                Class<T> classOfData)
    {
        List<T> data = new ArrayList<T>();

        File file = fileManager.getRawDataFile(timeSeries, source);
        file.getParentFile().mkdirs();

        if (file.exists())
        {
            data.addAll(fileManager.loadJsonList(file, classOfData).get());
        }

        return data;
    }

}
