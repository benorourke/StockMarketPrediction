package net.benorourke.stocks.userinterface;

import net.benorourke.stocks.framework.Configuration;
import net.benorourke.stocks.framework.Framework;

public class BackgroundThread extends Thread
{
    private final Framework framework;

    public BackgroundThread(Configuration configuration)
    {
        this.framework = new Framework(configuration);
    }

    @Override
    public void run()
    {

    }

}
