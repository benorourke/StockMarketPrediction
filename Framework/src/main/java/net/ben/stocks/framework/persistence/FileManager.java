package net.ben.stocks.framework.persistence;

import net.ben.stocks.framework.Configuration;

import java.io.File;

public class FileManager
{
    private final File workingDirectory;

    public FileManager(Configuration configuration)
    {
        this.workingDirectory = configuration.getWorkingDirectory();
    }

    public File getWorkingDirectory()
    {
        return workingDirectory;
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

    /**
     * Get the info file for a given TimeSeries.
     *
     * @param directory the directory containing the specific TimeSeries
     * @return
     */
    public File getTimeSeriesInfoFile(File directory)
    {
        return new File(directory + File.separator + "info.json");
    }

}
