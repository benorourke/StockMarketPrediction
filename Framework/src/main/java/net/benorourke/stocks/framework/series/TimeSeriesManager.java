package net.benorourke.stocks.framework.series;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.persistence.FileManager;
import net.benorourke.stocks.framework.persistence.store.DataStore;
import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.impl.Document;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;
import net.benorourke.stocks.framework.util.Initialisable;
import net.benorourke.stocks.framework.util.Tuple;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

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

    public boolean create(String name, String stock)
    {
        TimeSeries series = new TimeSeries(name, stock);
        timeSeries.add(series);
        sortSeries();

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

        if(info.exists()) info.delete();

        if(fileManager.writeJson(info, series))
        {
            Framework.info("Time Series Saved: " + series.toString() + " to " + info.toString());
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

            if(!infoFile.exists()) // There's a TimeSeries splash but the info file is missing
            {
                Framework.info("Timeseries info.json " + infoFile.getPath() + " is missing! Unable to load");
                continue;
            }

            Optional<TimeSeries> timeSeries = fileManager.loadJson(infoFile, TimeSeries.class);
            if(timeSeries != null)
                result.add(timeSeries.get());
            else
                Framework.error("Unable to load time series meta at " + infoFile.getPath());
        }

        sortSeries();
        return result;
    }

    public void sortSeries()
    {
        Collections.sort(timeSeries, Comparator.comparing(series -> series.getName().toLowerCase()));
    }

    //////////////////////////////////////////////////////////////////
    //      DATA COLLECTION / PRE-PROCESSING
    //////////////////////////////////////////////////////////////////

    public DataStore getDataStore(TimeSeries timeSeries)
    {
        return new DataStore(framework, timeSeries);
    }

    /**
     *
     * @param series
     * @param dataSource
     * @param data
     * @param overwrite whether to overwrite any existing data
     * @param <T>
     */
    public <T extends Data> void onDataCollected(TimeSeries series, DataSource<T> source, List<T> data,
                                                 boolean overwrite)
    {
        int newCount = 0;

        if (overwrite)
        {
            int count = getDataStore(series).writeRawData(source, data);
            Framework.info("Wrote (overwrite=true) " + count + " Data into TimeSeries " + series.toString());
        }
        else
        {
            Tuple<Integer, Integer> res = null;
            if (source.getDataType().equals(DataType.STOCK_QUOTE))
                res = getDataStore(series).injectRawQuotes((DataSource<StockQuote>) source,
                                                           (List<StockQuote>) data);
            else if (source.getDataType().equals(DataType.DOCUMENT))
                res = getDataStore(series).injectRawDocuments((DataSource<Document>) source,
                                                              (List<Document>) data);
            newCount = res.getA();
            Framework.info("Injected (overwrite=false) " + res.getB() + " Data into TimeSeries " + series.toString());
        }

        if (newCount != 0)
        {
            // Save the TimeSeries with the updated feedforward counts
            series.getRawDataCounts().put(source.getClass(), newCount);
            save(series);
        }
        else
            Framework.error("Error in writing any Data into TimeSeries " + series.toString()
                                + "(overwrite=" + overwrite + "). Collected Data Counts may be wrong.");
    }

    public <T extends Data> int cleanDuplicateData(TimeSeries series, DataSource<T> source)
    {
        DataStore store = getDataStore(series);

        int amountCleaned = 0;
        // Need to manually specify the types of data here, since we need to be careful of GSON type erasure:
        if (source.getDataType().equals(DataType.STOCK_QUOTE))
            amountCleaned = store.cleanDuplicateRawQuotes((DataSource<StockQuote>) source);
        else if (source.getDataType().equals(DataType.DOCUMENT))
            amountCleaned = store.cleanDuplicateRawDocuments((DataSource<Document>) source);

        if (amountCleaned != 0)
        {
            // Update the amounts if they've been cleaned
            Map<Class<? extends DataSource>, Integer> map = series.getRawDataCounts();
            Class<? extends DataSource> key = source.getClass();
            map.put(key, map.get(key) - amountCleaned);
            save(series);
        }
        return amountCleaned;
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

    //////////////////////////////////////////////////////////////////
    //      TRAINED MODELS
    //////////////////////////////////////////////////////////////////

    /**
     * To retrieve a model the model file and it's corresponding evaluation file must exist
     * @param timeSeries
     * @return
     */
    public List<String> getTrainedModels(TimeSeries timeSeries)
    {
        List<String> models = new ArrayList<>();

        File directory = fileManager.getTrainedDirectory(timeSeries);
        if (directory.exists())
        {
            for (File file : directory.listFiles())
            {
                String path = file.getPath();

                if (!path.endsWith(".model")) continue;

                String[] split = path.split(Pattern.quote(System.getProperty("file.separator")));
                String modelName = split[split.length - 1].replace(".model", "");

                File evaluation = fileManager.getModelEvaluationFile(timeSeries, modelName);
                if (evaluation.exists())
                    models.add(modelName);
            }
        }

        return models;
    }

    //////////////////////////////////////////////////////////////////
    //      MISC
    //////////////////////////////////////////////////////////////////

    public List<TimeSeries> getTimeSeries()
    {
        return timeSeries;
    }

}
