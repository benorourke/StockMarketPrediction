package net.ben.stocks.framework.persistence.gson;

import net.ben.stocks.framework.Framework;

import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class FileManager
{
    private final Framework framework;

    private File workingDirectory;

    public FileManager(Framework framework)
    {
        this.framework = framework;

        File defaultDir = FileSystemView.getFileSystemView().getDefaultDirectory(); // Takes about 100ms
        workingDirectory = new File(defaultDir + "/Stocks/");
        framework.debug("Using Directory " + workingDirectory.getPath());
    }

    public File getTimeSeriesParentDirectory()
    {
        return new File(workingDirectory + "/timeseries");
    }

    public File getTimeSeriesDirectory(String name)
    {
        return new File(getTimeSeriesParentDirectory() + name.toLowerCase());
    }

    public File getTimeSeriesInfoFile(File seriesDirectory)
    {
        return new File(seriesDirectory, "info.json");
    }

    public File getWorkingDirectory()
    {
        return workingDirectory;
    }

}
