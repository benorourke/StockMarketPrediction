package net.benorourke.stocks.userinterface.scene;

import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbarLayout;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

/**
 * All FXML files belonging to the Controller must have a root node, called root,
 */
public class Controller
{
    @FXML
    private Pane root;

    public Pane getRoot()
    {
        return root;
    }

    public void snackbar(SnackbarType type, String message)
    {
        JFXSnackbar snackbar = new JFXSnackbar(root);
        JFXSnackbarLayout content = new JFXSnackbarLayout(type.message, message, null);
        snackbar.enqueue(new JFXSnackbar.SnackbarEvent(content));
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
