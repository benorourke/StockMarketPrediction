package net.benorourke.stocks.framework;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Configuration
{
    private File workingDirectory;
    /**
     * How many threads to maintain in the pool to carry out tasks.
     */
    private int taskPoolSize;
    /**
     * Custom GSON Adaptersif the user is creating custom serializable objects.
     */
    private Map<Type, Object> gsonTypeAdapters;

    /**
     * Instantiate and initialise the Configuration with defaults.
     */
    public Configuration()
    {
        File defaultDir = FileSystemView.getFileSystemView().getDefaultDirectory(); // Takes about 100ms
        workingDirectory = new File(defaultDir + "/Stocks/");
        taskPoolSize = 5;
        gsonTypeAdapters = new HashMap<>();
    }

    public File getWorkingDirectory()
    {
        return workingDirectory;
    }

    public void setWorkingDirectory(File workingDirectory)
    {
        this.workingDirectory = workingDirectory;
    }

    public int getTaskPoolSize()
    {
        return taskPoolSize;
    }

    public void setTaskPoolSize(int taskPoolSize)
    {
        this.taskPoolSize = taskPoolSize;
    }

    public Map<Type, Object> getGsonTypeAdapters()
    {
        return gsonTypeAdapters;
    }

}
