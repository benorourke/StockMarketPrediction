package net.ben.stocks.framework.persistence.store;

import net.ben.stocks.framework.Framework;
import net.ben.stocks.framework.collection.datasource.DataSource;
import net.ben.stocks.framework.persistence.FileManager;
import net.ben.stocks.framework.series.TimeSeries;
import net.ben.stocks.framework.series.data.Data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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
        file.mkdirs();

        if(!file.exists()) file.delete(); // TODO- Maybe append?

        try
        {
            file.createNewFile();

            try (Writer writer = new FileWriter(file))
            {
                framework.getGson().toJson(data, writer);
                Framework.debug("Wrote " + data.size() + ": raw data to " + file.toString());
                writer.close();
                return true;
            }
            catch (IOException innerException)
            {
                Framework.error("Unable to write raw data to " + file.toString(), innerException);
                return false;
            }
        }
        catch (IOException outerException)
        {
            Framework.error("Unable to create raw data file", outerException);
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
