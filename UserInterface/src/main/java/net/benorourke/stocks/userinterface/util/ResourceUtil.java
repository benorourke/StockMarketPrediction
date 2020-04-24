package net.benorourke.stocks.userinterface.util;

import javafx.scene.text.Font;
import net.benorourke.stocks.userinterface.StockApplication;

import java.net.URL;

public class ResourceUtil
{

    private ResourceUtil() {}

    public static URL getResource(String path)
    {
        return StockApplication.class.getResource(path);
    }

}
