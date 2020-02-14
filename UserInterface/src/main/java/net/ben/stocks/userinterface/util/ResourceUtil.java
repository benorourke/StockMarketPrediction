package net.ben.stocks.userinterface.util;

import net.ben.stocks.userinterface.StockApplication;

import java.net.URL;

public class ResourceUtil
{

    private ResourceUtil() {}

    public static URL getResource(String path)
    {
        return StockApplication.class.getResource(path);
    }

}
