package net.benorourke.stocks.framework.persistence.store;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.persistence.FileManager;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.series.data.Data;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private String sourceToString(Class<? extends DataSource> source)
    {
        return source.getSimpleName().toLowerCase();
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
    //        - processeddocument.json
    //        - processedstockquote.json

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

    public List<Data> loadRawData(Class<? extends DataSource>... source)
    {
        // TODO
        return null;
    }

    public Map<Class<? extends DataSource>, List<Data>> getAllRawData(
                Class<? extends DataSource>... sources)
    {
        Map<Class<? extends DataSource>, List<Data>> data = new HashMap<>();
        for (Class<? extends DataSource> clazz : sources)
        {
            data.put(clazz, loadRawData(sources));
        }
        return data;
    }

}
