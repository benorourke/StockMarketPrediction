package net.benorourke.stocks.userinterface.util;

import javafx.scene.paint.Color;

/**
 * A collection of utilities for managing JavaFX-specific features.
 */
public class JavaFXUtil
{

    private JavaFXUtil() {}

    /**
     * Convert a JavaFX colour to #RRGGBB format
     *
     * @param color the colour
     * @return the formatted string
     */
    public static String toRGB(Color color)
    {
        return String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255),
                             (int) (color.getBlue() * 255));
    }

}
