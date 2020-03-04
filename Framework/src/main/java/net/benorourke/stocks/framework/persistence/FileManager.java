package net.benorourke.stocks.framework.persistence;

import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import net.benorourke.stocks.framework.Configuration;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.model.data.ModelData;
import net.benorourke.stocks.framework.model.data.ProcessedCorpus;
import net.benorourke.stocks.framework.series.TimeSeries;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;

public class FileManager
{
    private static final char CSV_SEPARATOR = ',';

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

    private void createFile(File file, boolean deleteIfExists) throws IOException
    {
        if(!file.getParentFile().exists())
            file.getParentFile().mkdirs();


        if(deleteIfExists && file.exists())
            file.delete();

        if(!file.exists())
        {
            file.createNewFile();
        }
    }

    //////////////////////////////////////////////////////////////////
    //      GSON
    //////////////////////////////////////////////////////////////////

    public boolean writeJson(File file, Object object)
    {
        try
        {
            createFile(file, true);
        }
        catch (IOException e) { return false; }

        try (Writer writer = new FileWriter(file))
        {
            framework.getGson().toJson(object, writer);
            writer.close();
            return true;
        }
        catch (IOException e)
        {
            Framework.error("Unable to write " + object.getClass().getSimpleName() + " to json", e);
            return false;
        }
    }

    /**
     * Returns an Optional with null inside if it the object couldn't be loaded
     * @param file
     * @param classOfType
     * @param <T>
     * @return
     */
    public <T> Optional<T> loadJson(File file, Class<T> classOfType)
    {
        if(!file.exists()) Optional.empty();

        try
        {
            return Optional.of(framework.getGson().fromJson(new FileReader(file), classOfType));
        }
        catch (FileNotFoundException e)
        {
            Framework.error("Unable to load " + classOfType.getSimpleName()
                                        + " from json file (" + file.toString() + ")", e);
            return Optional.empty();
        }
    }

    public <T> Optional<Collection<T>> loadJsonList(File file, TypeToken token)
    {
        if(!file.exists()) Optional.empty();

        Type type = token.getType();
        try
        {
            JsonReader reader = new JsonReader(new FileReader(file));
            Collection<T> data = framework.getGson().fromJson(reader, type);

            return Optional.of(data);
        }
        catch (FileNotFoundException e)
        {
            Framework.error("Unable to load list of " + type.toString()
                    + " from json file (" + file.toString() + ")", e);
            return Optional.empty();
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

    public File getRawDataStoreDirectory(TimeSeries timeSeries)
    {
        return new File(getTimeSeriesDirectory(timeSeries), "raw");
    }

    public File getRawDataFile(TimeSeries timeSeries, Class<? extends DataSource> clazz)
    {
        return new File(getRawDataStoreDirectory(timeSeries)
                            + File.separator + clazz.getSimpleName().toLowerCase() + ".json");
    }

    public File getProcessedDataStoreDirectory(TimeSeries timeSeries)
    {
        return new File(getTimeSeriesDirectory(timeSeries), "processed");
    }

    public File getProcessedCorpusFile(TimeSeries timeSeries)
    {
        return new File(getProcessedDataStoreDirectory(timeSeries) + File.separator + "processed.csv");
    }

    public boolean writeProcessedCorpus(TimeSeries timeSeries, ProcessedCorpus corpus)
    {
        File file = getProcessedCorpusFile(timeSeries);

        try
        {
            createFile(file, true);
        }
        catch (IOException e) { return false; }

        final String[] HEADERS = new String[] {"inOPEN", "inCLOSE", "inHIGH", "inLOW", "inVOLUME",
                                               "outOPEN", "outCLOSE", "outHIGH", "outLOW", "outVOLUME"}; // TODO make static when done
        try
        {
            FileWriter out = new FileWriter(file);
            try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(HEADERS)))
            {
                corpus.getData().forEach(data ->
                {
                    try
                    {
                        double[] features = data.getFeatures(); // TODO
                        double[] labels = data.getLabels(); // TODO
                        printer.printRecord(features[0],features[1],features[2],features[3],features[4],
                                            labels[0],labels[1],labels[2],labels[3],labels[4]);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                });
            }
        }
        catch (Exception e)
        {
            Framework.error("Unable to write process corpus to " + file.toString(), e);
            return false;
        }

        return true;
    }

}
