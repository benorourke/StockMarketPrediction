package net.benorourke.stocks.framework.persistence.store;

import com.google.gson.reflect.TypeToken;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.persistence.FileManager;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.impl.Document;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;
import net.benorourke.stocks.framework.util.Tuple;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    public boolean rawDataExists(Class<? extends DataSource> source)
    {
        return fileManager.getRawDataFile(timeSeries, source).exists();
    }

    /**
     * Will replace any existing data.
     *
     * 0 indicates an error.
     *
     * @param source
     * @param data
     * @param <T>
     * @return the amount of data written. 0 if unsuccessful
     */
    public <T extends Data> int writeRawData(Class<? extends DataSource> source, Collection<T> data)
    {
        File file = fileManager.getRawDataFile(timeSeries, source);
        file.getParentFile().mkdirs();

        if(!file.exists()) file.delete(); // TODO- Maybe append?

        if(fileManager.writeJson(file, data))
        {
            Framework.debug("Wrote " + data.size() + ": raw feedforward to " + file.toString());
            return data.size();
        }
        else
        {
            Framework.error("Unable to write raw feedforward to " + file.toString());
            return 0;
        }
    }

    /**
     * Will add to any existing data
     *
     * 0 indicates an error.
     *
     * @param source
     * @param toInject
     * @param <T>
     * @return A: the total data written, B: the number of data injected
     */
    public <T extends Data> Tuple<Integer, Integer> injectRawData(Class<? extends DataSource<T>> source,
                                                                  Collection<T> toInject)
    {
        Framework.debug("Injection: " + source.getName());
        List<T> existingData = loadRawData(source);
        existingData.addAll(toInject);
        return new Tuple<>(writeRawData(source, existingData), toInject.size());
    }

    public <T extends Data> List<T> loadRawData(Class<? extends DataSource<T>> source)
    {
        File file = fileManager.getRawDataFile(timeSeries, source);
        file.getParentFile().mkdirs();

        List<T> data = new ArrayList<>();
        if (file.exists())
        {
            TypeToken typeToken = new TypeToken<List<T>>(){};
            Collection<T> loaded = fileManager.<T>loadJsonList(file, typeToken).get();
            data.addAll(loaded);
        }

        return data;
    }

    public List<StockQuote> loadRawStockQuotes(Class<? extends DataSource<StockQuote>> source)
    {
        return loadRawData(source);
    }

    public List<Document> loadRawDocuments(Class<? extends DataSource<Document>> source)
    {
        return loadRawData(source);
    }

//    public List<StockQuote> loadRawStockQuotes(Class<? extends DataSource<StockQuote>> source)
//    {
//        File file = fileManager.getRawDataFile(timeSeries, source);
//        file.getParentFile().mkdirs();
//
//        List<StockQuote> data = new ArrayList<>();
//        if (file.exists())
//        {
//            TypeToken typeToken = new TypeToken<List<StockQuote>>(){};
//            Collection<Object> loaded = fileManager.loadJsonList(file, typeToken).get();
//            data.addAll(loaded.stream().map(o -> (StockQuote) o).collect(Collectors.toList()));
//        }
//
//        return data;
//    }
//
//    public List<Document> loadRawDocuments(Class<? extends DataSource<Document>> source)
//    {
//        File file = fileManager.getRawDataFile(timeSeries, source);
//        file.getParentFile().mkdirs();
//
//        List<Document> data = new ArrayList<>();
//        if (file.exists())
//        {
//            TypeToken typeToken = new TypeToken<List<Document>>(){};
//            Collection<Object> loaded = fileManager.loadJsonList(file, typeToken).get();
//            data.addAll(loaded.stream().map(o -> (Document) o).collect(Collectors.toList()));
//        }
//
//        return data;
//    }

}
