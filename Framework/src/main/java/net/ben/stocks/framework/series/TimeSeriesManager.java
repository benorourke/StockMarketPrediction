package net.ben.stocks.framework.series;

import net.ben.stocks.framework.util.Initialisable;

import java.io.File;

public class TimeSeriesManager implements Initialisable
{
    private File storageDirectory;



    public TimeSeriesManager(File storageDirectory)
    {
        this.storageDirectory = storageDirectory;
    }

    @Override
    public void initialise()
    {
        refresh();
    }

    private void refresh()
    {
        if(!storageDirectory.exists())
            storageDirectory.mkdir();
    }

    public File getStorageDirectory()
    {
        return storageDirectory;
    }

    public void setStorageDirectory(File storageDirectory)
    {
        this.storageDirectory = storageDirectory;
    }

}
