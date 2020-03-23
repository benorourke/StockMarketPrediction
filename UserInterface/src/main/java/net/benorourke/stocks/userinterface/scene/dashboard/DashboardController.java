package net.benorourke.stocks.userinterface.scene.dashboard;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.userinterface.StockApplication;
import net.benorourke.stocks.userinterface.exception.InflationException;
import net.benorourke.stocks.userinterface.scene.Controller;
import net.benorourke.stocks.userinterface.scene.SceneHelper;
import net.benorourke.stocks.userinterface.util.FontFamily;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardController extends Controller
{
    private static final double HEADER_SIZE = 24;
    private static final String NAV_BUTTON_STYLE_CLASS = "nav-row";
    private static final String SERIES_ROW_FXML = "/dashboard-series.fxml";

    private final DashboardModel model;

    @FXML private AnchorPane root;
    @FXML private Label headerLabel;
    @FXML private VBox paneVBox;

    private List<HBox> navRows;

    public DashboardController()
    {
        model = new DashboardModel(this);
        navRows = new ArrayList<>();
    }

    @FXML
    public void initialize()
    {
        headerLabel.setFont(FontFamily.OPENSANS_BOLD.get(HEADER_SIZE));

        navRows.addAll(getNavBarBoxes(root));
        for (HBox row : navRows)
        {
            row.setOnMouseClicked( event ->
            {
                Framework.debug("Clicked row " + row.getId());
            });
        }

        model.loadTimeSeries( () -> updateTimeSeries() );
    }

    private List<HBox> getNavBarBoxes(Parent parent)
    {
        List<HBox> boxes = new ArrayList<>();
        for (Node child : parent.getChildrenUnmodifiable())
        {
            // Recurse the function to get all nav bar boxes contained within this child
            if (child instanceof Parent) boxes.addAll(getNavBarBoxes((Parent) child));

            if (child instanceof HBox && child.getStyleClass().contains(NAV_BUTTON_STYLE_CLASS))
                boxes.add((HBox) child);
        }
        return boxes;
    }

    private void updateTimeSeries()
    {
        StockApplication.debug("Updating time series");
        paneVBox.getChildren().clear();
        paneVBox.getChildren().addAll(model.getTimeSeries()
                                                .stream()
                                                .map(t -> createTimeSeries(t))
                                                .collect(Collectors.toList()));
    }

    private Node createTimeSeries(TimeSeries series)
    {
        StockApplication.debug("Creating Node from TimeSeries");
        try
        {
            Parent parent = SceneHelper.inflate(SERIES_ROW_FXML);
            return parent;
        }
        catch (InflationException e)
        {
            StockApplication.error("Unable to inflate " + SERIES_ROW_FXML, e);
            return null;
        }
    }

}
