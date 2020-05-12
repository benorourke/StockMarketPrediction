package net.benorourke.stocks.userinterface.util;

import net.benorourke.stocks.userinterface.StockApplication;

import java.net.URL;

/**
 * A utility class for accessing resources.
 */
public class ResourceUtil
{

    private ResourceUtil() {}

    /**
     * Get a resource by a path.
     *
     * @param path the path
     * @return the resource
     */
    public static URL getResource(String path)
    {
        return StockApplication.class.getResource(path);
    }

}
