package net.ben.stocks.framework;

import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class Configuration
{
    private File workingDirectory;

    /**
     * Instantiate and initialise the Configuration with defaults.
     */
    public Configuration()
    {
        File defaultDir = FileSystemView.getFileSystemView().getDefaultDirectory(); // Takes about 100ms
        workingDirectory = new File(defaultDir + "/Stocks/");
    }

    public File getWorkingDirectory()
    {
        return workingDirectory;
    }

    public void setWorkingDirectory(File workingDirectory)
    {
        this.workingDirectory = workingDirectory;
    }
}
