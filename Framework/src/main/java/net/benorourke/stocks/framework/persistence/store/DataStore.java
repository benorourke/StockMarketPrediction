package net.benorourke.stocks.framework.persistence.store;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.persistence.FileManager;
import net.benorourke.stocks.framework.persistence.gson.ParameterizedTypes;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.IdentifiableData;
import net.benorourke.stocks.framework.series.data.impl.Document;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;
import net.benorourke.stocks.framework.util.Tuple;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * A TimeSeries-specific class that handles data operations for a TimeSeries.
 */
public class DataStore
{
    private final Framework framework;
    private final FileManager fileManager;
    private final TimeSeries timeSeries;

    /**
     * Create a new instance.
     *
     * @param framework the framework instance
     * @param timeSeries the timeseries this store is for
     */
    public DataStore(Framework framework, TimeSeries timeSeries)
    {
        this.framework = framework;
        this.fileManager = framework.getFileManager();
        this.timeSeries = timeSeries;
    }

    //////////////////////////////////////////////////////////////////
    //      RAW DATA
    //////////////////////////////////////////////////////////////////

    /**
     * Will replace any existing data.
     *
     * 0 indicates an error/no data was written.
     *
     * @param source the source to write data for
     * @param data the data to write
     * @param <T> the inferred type
     * @return the amount of data written. 0 if unsuccessful/no data was written
     */
    public <T extends Data> int writeRawData(DataSource source, Collection<T> data)
    {
        File file = fileManager.getRawDataFile(timeSeries, source);
        file.getParentFile().mkdirs();

        if(!file.exists()) file.delete();

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
     * @param source the source to inject data for
     * @param toInject the data to inject
     * @param <T> the inferred type
     * @return A: the total data written, B: the number of data injected
     */
    private <T extends Data> Tuple<Integer, Integer> injectRawData(DataSource<T> source, List<T> toInject,
                                                                   ParameterizedType listType)
    {
        Framework.debug("Injection: " + source.getName());
        List<T> existingData = loadRawData(source, listType);
        existingData.addAll(toInject);
        return new Tuple<>(writeRawData(source, existingData), toInject.size());
    }

    public Tuple<Integer, Integer> injectRawQuotes(DataSource<StockQuote> source, List<StockQuote> toInject)
    {
        return injectRawData(source, toInject, ParameterizedTypes.LIST_STOCKQUOTE);
    }

    public Tuple<Integer, Integer> injectRawDocuments(DataSource<Document> source, List<Document> toInject)
    {
        return injectRawData(source, toInject, ParameterizedTypes.LIST_DOCUMENT);
    }

    /**
     * Load a set of raw data from a file.
     *
     * @param source the datasource to load data from
     * @param listType needed to prevent GSON type erasure
     * @param <T>
     * @return the raw data
     */
    private <T extends Data> List<T> loadRawData(DataSource<T> source, ParameterizedType listType)
    {
        File file = fileManager.getRawDataFile(timeSeries, source);
        file.getParentFile().mkdirs();

        List<T> data = new ArrayList<>();
        if (file.exists())
        {
            Collection<T> loaded = fileManager.<T>loadJsonList(file, listType).get();
            data.addAll(loaded);
        }

        return data;
    }

    public List<StockQuote> loadRawStockQuotes(DataSource<StockQuote> source)
    {
        return loadRawData(source, ParameterizedTypes.LIST_STOCKQUOTE);
    }

    public List<Document> loadRawDocuments(DataSource<Document> source)
    {
        return loadRawData(source, ParameterizedTypes.LIST_DOCUMENT);
    }

    /**
     * Remove any duplicate raw data from a datasource.
     *
     * @param source the source to remove duplicates from
     * @param listType needed to prevent GSON type erasure
     * @param <T> the inferred type
     * @return the number o data removed
     */
    private <T extends Data> int cleanDuplicateRawData(DataSource<T> source, ParameterizedType listType)
    {
        List<T> data = loadRawData(source, listType);

        List<T> cleaned = new ArrayList<T>();
        outer: for (T elem : data)
        {
            for (T elem2 : cleaned)
            {
                if (elem2.isDuplicate(elem))
                    continue outer;
            }

            // If it reaches here (outer was continued), then we know the cleaned dataset does not already contain this
            cleaned.add(elem);
        }

        if (data.size() != cleaned.size())
        {
            writeRawData(source, cleaned);
            return data.size() - cleaned.size();
        }
        else
            return 0;
    }

    /**
     * Remove a specific piece of raw data.
     *
     * @param source the data source to remove the data from
     * @param listType  needed to prevent GSON type erasure
     * @param toRemove the ID of the data to remove
     * @param <T>
     * @return the amount remove. Non-zero if data was removed
     */
    public <T extends Data> int removeRawData(DataSource<T> source, ParameterizedType listType, UUID toRemove)
    {
        List<T> data = loadRawData(source, listType);
        List<T> filtered = new ArrayList<>();

        for (T elem : data)
        {
            if (elem instanceof IdentifiableData && ((IdentifiableData) elem).getId().equals(toRemove))
                continue;

            filtered.add(elem);
        }

        if (data.size() != filtered.size())
        {
            writeRawData(source, filtered);
            return data.size() - filtered.size();
        }
        else
            return 0;
    }

    public int cleanDuplicateRawQuotes(DataSource<StockQuote> source)
    {
        return cleanDuplicateRawData(source, ParameterizedTypes.LIST_STOCKQUOTE);
    }

    public int cleanDuplicateRawDocuments(DataSource<Document> source)
    {
        return cleanDuplicateRawData(source, ParameterizedTypes.LIST_DOCUMENT);
    }

}
