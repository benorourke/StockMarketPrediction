package net.ben.stocks.userinterface;

import net.ben.stocks.framework.Configuration;
import net.ben.stocks.framework.Framework;

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
