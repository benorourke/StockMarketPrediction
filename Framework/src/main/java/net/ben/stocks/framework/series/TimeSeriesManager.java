package net.ben.stocks.framework.series;

import net.ben.stocks.framework.Framework;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class TimeSeriesManager
{
    private final Framework framework;

    public TimeSeriesManager(Framework framework)
    {
        this.framework = framework;

        LoggerUtil.debug(getStoredTimeSeries().size() + " time series found");
    }

    private List<TimeSeries> getStoredTimeSeries()
    {
        File storageDirectory = framework.getFileManager().getTimeSeriesParentDirectory();

        if (!storageDirectory.exists())
            storageDirectory.mkdir();

        List<TimeSeries> result = new ArrayList<TimeSeries>();

        if (storageDirectory.listFiles() == null)
            return result;

        for (File file : storageDirectory.listFiles())
        {
            if (!file.isDirectory()) continue;

            File infoFile = framework.getFileManager().getTimeSeriesInfoFile(file);
            try
            {
                result.add(framework.getGson().fromJson(new FileReader(infoFile), TimeSeries.class));
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        return result;
    }

}
