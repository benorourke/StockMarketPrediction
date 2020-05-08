package net.benorourke.stocks.userinterface.util;

import javafx.scene.paint.Color;

public class JavaFXUtil
{

    private JavaFXUtil() {}

    public static String toRGB(Color color)
    {
        return String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255),
                             (int) (color.getBlue() * 255));
    }

}
