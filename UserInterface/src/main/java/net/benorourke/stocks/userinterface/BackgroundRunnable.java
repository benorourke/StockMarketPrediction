package net.benorourke.stocks.userinterface;

import net.benorourke.stocks.framework.Framework;

/**
 * A task to be executed on the background thread.
 */
public interface BackgroundRunnable
{

    /**
     * @param framework the framework instance supplied by the background thread upon execution
     */
    void run(Framework framework);

}