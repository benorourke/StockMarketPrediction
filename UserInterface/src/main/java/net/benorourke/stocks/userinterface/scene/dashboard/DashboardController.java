package net.benorourke.stocks.userinterface.scene.dashboard;

import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.userinterface.StockApplication;
import net.benorourke.stocks.userinterface.exception.InflationException;
import net.benorourke.stocks.userinterface.scene.Controller;
import net.benorourke.stocks.userinterface.scene.SceneHelper;
import net.benorourke.stocks.userinterface.scene.dashboard.panes.DashboardPane;
import net.benorourke.stocks.userinterface.scene.dashboard.panes.PaneHandler;
import net.benorourke.stocks.userinterface.scene.dashboard.panes.TrainingPaneHandler;
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

    //////////////////////////////////////////////////////////////////
    //      NAV BAR STUFF
    //////////////////////////////////////////////////////////////////
    @FXML private AnchorPane root;
    @FXML private Label headerLabel; // Nav bar header
    @FXML private VBox paneVBox;

    private List<HBox> navRows;

    //////////////////////////////////////////////////////////////////
    //      PANE
    //////////////////////////////////////////////////////////////////
    @FXML private TabPane tabPane;
    @FXML private Tab homeTab, collectionTab, preprocessingTab, trainingTab, evaluationTab;

    //////////////////////////////////////////////////////////////////
    //      PANE HANDLERS
    //////////////////////////////////////////////////////////////////
    private PaneHandler[] paneHandlers;

    // TRAINING:
    @FXML private JFXComboBox<String> trainingComboBox;
    @FXML private Label trainingHandlerCount;
    private TrainingPaneHandler trainingPaneHandler;

    //////////////////////////////////////////////////////////////////
    //      MISC
    //////////////////////////////////////////////////////////////////
    @FXML private Label tasksRunningLabel;

    public DashboardController()
    {
        model = new DashboardModel(this);
        navRows = new ArrayList<>();
    }

    @FXML
    public void initialize()
    {
        // Initialise Navbars & nav-pane
        headerLabel.setFont(FontFamily.OPENSANS_BOLD.get(HEADER_SIZE));
        navRows.addAll(getNavBarBoxes(root));
        for (int i = 0; i < navRows.size(); i ++)
        {
            HBox row = navRows.get(i);
            DashboardPane paneFor = DashboardPane.values()[i];

            row.setOnMouseClicked( event ->
            {
                String id = row.getId();

                SingleSelectionModel<Tab> model = tabPane.getSelectionModel();
                model.select(paneFor.ordinal());

                Framework.debug("Clicked row " + row.getId() + " (" + paneFor.toString() + ")");
            });
        }

        model.loadTimeSeries( () -> updateTimeSeries() );

        // Initialise pane handlers
        paneHandlers = new PaneHandler[DashboardPane.values().length];
        paneHandlers[DashboardPane.PRE_PROCESSING.ordinal()] =
                new TrainingPaneHandler(model, trainingHandlerCount, trainingComboBox);

        for (PaneHandler handler : paneHandlers)
        {
            // TODO Remove null check; shouldn't be null when completed
            if (handler == null) continue;

            handler.initialise();
        }
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
