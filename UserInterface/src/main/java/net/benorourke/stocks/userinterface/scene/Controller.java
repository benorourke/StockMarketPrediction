package net.benorourke.stocks.userinterface.scene;

import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbarLayout;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

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

    public enum SnackbarType
    {
        ERROR("Error");

        private String message;

        SnackbarType(String message)
        {
            this.message = message;
        }
    }

}
