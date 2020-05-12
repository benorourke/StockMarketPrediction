package net.benorourke.stocks.userinterface.scene;

import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbarLayout;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import net.benorourke.stocks.framework.util.Nullable;

/**
 * The base controller class providing easy snackbar functionality.
 *
 * All FXML files belonging to the Controller must have a root node, called root,
 */
public class Controller
{
    @FXML
    private Pane root;

    @Nullable
    private JFXSnackbar lastSnackbar;

    public Pane getRoot()
    {
        return root;
    }

    /**
     * Display a snackbar at the bottom of the controller.
     *
     * @param type the type of message
     * @param message the message itself
     */
    public void snackbar(SnackbarType type, String message)
    {
        if (lastSnackbar != null)
            lastSnackbar.setVisible(false);

        JFXSnackbar snackbar = new JFXSnackbar(root);
        JFXSnackbarLayout content = new JFXSnackbarLayout(type.message, message, null);
        snackbar.enqueue(new JFXSnackbar.SnackbarEvent(content));
        lastSnackbar = snackbar;
    }

    public void snackbarNullTimeSeries()
    {
        snackbar(SnackbarType.INFO, "Select a Time Series before performing this action!");
    }

    public enum SnackbarType
    {
        INFO("Info"),
        ERROR("Error"),
        NAVIGATION_RESTRICTION("Restriction");

        private String message;

        SnackbarType(String message)
        {
            this.message = message;
        }
    }

}
