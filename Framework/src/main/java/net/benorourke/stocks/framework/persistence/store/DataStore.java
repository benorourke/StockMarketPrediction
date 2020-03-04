package net.benorourke.stocks.framework.persistence.store;

import com.google.gson.reflect.TypeToken;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.persistence.FileManager;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.impl.Document;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;
import net.benorourke.stocks.framework.util.DateUtil;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

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

    public List<StockQuote> loadRawStockQuotes(Class<? extends DataSource<StockQuote>> source)
    {
        File file = fileManager.getRawDataFile(timeSeries, source);
        file.getParentFile().mkdirs();

        List<StockQuote> data = new ArrayList<StockQuote>();
        if (file.exists())
        {
            TypeToken typeToken = new TypeToken<List<StockQuote>>(){};
            Collection<Object> loaded = fileManager.loadJsonList(file, typeToken).get();
            data.addAll(loaded.stream().map(o -> (StockQuote) o).collect(Collectors.toList()));
        }

        return data;
    }

    public List<Document> loadRawDocuments(Class<? extends DataSource<Document>> source)
    {
        File file = fileManager.getRawDataFile(timeSeries, source);
        file.getParentFile().mkdirs();

        List<Document> data = new ArrayList<Document>();
        if (file.exists())
        {
            TypeToken typeToken = new TypeToken<List<Document>>(){};
            Collection<Object> loaded = fileManager.loadJsonList(file, typeToken).get();
            data.addAll(loaded.stream().map(o -> (Document) o).collect(Collectors.toList()));
        }

        // TODO: Remove
        Framework.debug("Looping");
        for ( Document document : data)
        {
            Framework.debug("Loaded document on " + DateUtil.formatDetailed(document.getDate()));
        }

        return data;
    }

}
