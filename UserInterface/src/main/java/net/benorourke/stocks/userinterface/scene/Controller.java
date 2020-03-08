package net.benorourke.stocks.userinterface.scene;

import javafx.fxml.FXML;
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

    public void bind(Region bind, Region to)
    {
        bind.prefWidthProperty().bind(to.prefWidthProperty());
        bind.prefHeightProperty().bind(to.prefHeightProperty());
    }

    public void bind(Region bind, Region to, double coefficient)
    {
        bind.prefWidthProperty().bind(to.widthProperty().multiply(coefficient));
        bind.prefHeightProperty().bind(to.heightProperty().multiply(coefficient));
    }

    public void bindX(Region bind, Region to, double coefficient)
    {
        bind.prefWidthProperty().bind(to.widthProperty().multiply(coefficient));
    }

    public void bindX(Region bind, Region to)
    {
        bind.prefWidthProperty().bind(to.widthProperty());
    }

    public void bindY(Region bind, Region to, double coefficient)
    {
        bind.prefHeightProperty().bind(to.heightProperty().multiply(coefficient));
    }

    public void bindY(Region bind, Region to)
    {
        bind.prefHeightProperty().bind(to.heightProperty());
    }

}
