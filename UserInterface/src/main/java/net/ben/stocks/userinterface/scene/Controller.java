package net.ben.stocks.userinterface.scene;

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

}
