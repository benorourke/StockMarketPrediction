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
        framework.log("Using Directory " + workingDirectory.getPath());
    }

    public File getTimeSeriesInfoFile(File directory)
    {
        return new File(directory, "info.json");
    }

    public File getDirectory()
    {
        return workingDirectory;
    }

}
