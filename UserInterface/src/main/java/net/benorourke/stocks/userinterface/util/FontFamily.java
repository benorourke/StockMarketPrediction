package net.benorourke.stocks.userinterface.util;

import javafx.scene.text.Font;
import net.benorourke.stocks.framework.util.Nullable;
import net.benorourke.stocks.userinterface.StockApplication;

import java.util.HashMap;
import java.util.Map;

public enum FontFamily
{
    OPENSANS_BOLD("/fonts/OpenSans-Bold.ttf");

    private final String path;

    private Map<Integer, Font> loadedFonts;

    FontFamily(String path)
    {
        this.path = path;
        loadedFonts = new HashMap<>();
    }

    public Font get(double size)
    {
        int intSize = (int) Math.floor(size);
        if (!loadedFonts.containsKey(intSize))
        {
            Font font = Font.loadFont(ResourceUtil.getResource(path).toExternalForm(), size);
            loadedFonts.put(intSize, font);
            StockApplication.info("Loaded font " + path + " (size=" + size + ", null=" + (font == null) + ")");
        }

        return loadedFonts.get(intSize);
    }

}
