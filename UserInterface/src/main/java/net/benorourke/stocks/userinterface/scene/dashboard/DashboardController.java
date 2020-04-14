package net.benorourke.stocks.userinterface.scene.dashboard;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.util.Tuple;
import net.benorourke.stocks.userinterface.StockApplication;
import net.benorourke.stocks.userinterface.exception.InflationException;
import net.benorourke.stocks.userinterface.scene.Controller;
import net.benorourke.stocks.userinterface.scene.SceneHelper;
import net.benorourke.stocks.userinterface.scene.dashboard.panes.*;
import net.benorourke.stocks.userinterface.util.FontFamily;

import java.util.ArrayList;
import java.util.List;

public class DashboardController extends Controller
{
    private static final double HEADER_SIZE = 24;
    private static final String NAV_BUTTON_STYLE_CLASS = "nav-row";
    private static final String NAV_BUTTON_ICON_STYLE_CLASS = "nav-icon";
    private static final String NAV_BUTTON_TEXT_STYLE_CLASS = "nav-text";
    private static final String NAV_BUTTON_ICON_SELECTED_STYLE_CLASS = "nav-icon-selected";
    private static final String NAV_BUTTON_TEXT_SELECTED_STYLE_CLASS = "nav-text-selected";
    private static final String SERIES_ROW_FXML = "/dashboard-series.fxml";

    private static final Color[] SERIES_CIRCLE_FILLS = new Color[]
    {
            Color.rgb(0x1E, 0x90, 0xFF),    Color.rgb(0xFA, 0xA9, 0x16),
            Color.rgb(0x2A, 0xF5, 0xFF),    Color.rgb(0xFF, 0xEE, 0xDD),
            Color.rgb(0xB4, 0xED, 0xD2),    Color.rgb(0xF7, 0xAF, 0x9D),
            Color.rgb(0xF7, 0xE3, 0xAF),    Color.rgb(0xE4, 0xFD, 0xE1),
            Color.rgb(0xFF, 0xFC, 0xFF),    Color.rgb(0x32, 0xE8, 0x75),
    };

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

    // COLLECTION:
    @FXML private JFXComboBox<String> collectionComboBox;
    @FXML private TabPane collectionTabPane;

    // TRAINING:
    @FXML private Label trainingHandlerCount;
    @FXML private JFXComboBox<String> trainingComboBox;
    @FXML private VBox trainingFieldBox;
    @FXML private JFXButton trainButton;
    private TrainingPaneHandler trainingPaneHandler;

    // EVALUATION:
    @FXML private JFXComboBox<String> evaluationComboBox;
    @FXML private LineChart<String, Number> evaluationChart;
    private EvaluationPaneHandler evaluationPaneHandler;

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

                selectNavbarBox(row, paneFor);
            });
        }

        model.acquireTimeSeries( () -> updateTimeSeries() );

        // Initialise pane handlers
        paneHandlers = new PaneHandler[DashboardPane.values().length];
        paneHandlers[DashboardPane.COLLECTION.ordinal()] =
                new CollectionPaneHandler(this, model, collectionComboBox, collectionTabPane);
        paneHandlers[DashboardPane.PRE_PROCESSING.ordinal()] =
                new TrainingPaneHandler(this, model,
                                        trainingHandlerCount, trainingComboBox, trainingFieldBox, trainButton);
        paneHandlers[DashboardPane.EVALUATION.ordinal()] =
                new EvaluationPaneHandler(this, model, evaluationComboBox, evaluationChart);

        for (PaneHandler handler : paneHandlers)
        {
            if (handler == null) continue;  // TODO Remove null check; shouldn't be null when completed

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

    private void selectNavbarBox(HBox toSelect, DashboardPane paneFor)
    {
        SingleSelectionModel<Tab> model = tabPane.getSelectionModel();
        model.select(paneFor.ordinal());

        for (HBox row : navRows)
            changeNavbarClassRecursive(row, toSelect.equals(row));
    }

    private void changeNavbarClassRecursive(Parent node, boolean selected)
    {
        for (Node child : node.getChildrenUnmodifiable())
        {
            if (child instanceof Parent)
                changeNavbarClassRecursive((Parent) child, selected);
            else
                changeNavbarClass(child, selected);
        }

        changeNavbarClass(node, selected);
    }

    private void changeNavbarClass(Node node, boolean selected)
    {
        if (node.getStyleClass().contains(NAV_BUTTON_ICON_STYLE_CLASS)
                || node.getStyleClass().contains(NAV_BUTTON_ICON_SELECTED_STYLE_CLASS))
        {
            node.getStyleClass().clear();
            node.getStyleClass().add(selected ? NAV_BUTTON_ICON_SELECTED_STYLE_CLASS : NAV_BUTTON_ICON_STYLE_CLASS);
        }
        else if (node.getStyleClass().contains(NAV_BUTTON_TEXT_STYLE_CLASS)
                || node.getStyleClass().contains(NAV_BUTTON_TEXT_SELECTED_STYLE_CLASS))
        {
            node.getStyleClass().clear();
            node.getStyleClass().add(selected ? NAV_BUTTON_TEXT_SELECTED_STYLE_CLASS : NAV_BUTTON_TEXT_STYLE_CLASS);
        }
    }

    private void updateTimeSeries()
    {
        StockApplication.debug("Updating time series");
        paneVBox.getChildren().clear();
        for (TimeSeries series : model.getTimeSeries())
        {
            try
            {
                Tuple<FXMLLoader, Parent> tuple = SceneHelper.inflate(SERIES_ROW_FXML);
                FXMLLoader loader = tuple.getA();
                Parent parent = tuple.getB();

                Circle circle = (Circle) loader.getNamespace().get("seriesCircle");
                Label name = (Label) loader.getNamespace().get("seriesName");
                Label stock = (Label) loader.getNamespace().get("seriesStock");

                int index = series.getId().hashCode() % 10;
                index = (index < 0) ? index * -1 : index; // Prevent negative indices
                circle.setFill(SERIES_CIRCLE_FILLS[index]);
                name.setText(series.getName());
                stock.setText(series.getStock().getExchange().getShortName() + ":" + series.getStock().getTicker());

                parent.setOnMouseClicked(event -> changeTimeSeries(series));
                paneVBox.getChildren().add(parent);
            }
            catch (InflationException e)
            {
                StockApplication.error("Unable to inflate " + SERIES_ROW_FXML, e);
            }
        }

        if (model.getTimeSeries().size() > 0)
            changeTimeSeries(model.getTimeSeries().get(0));

    }

    public void changeTimeSeries(TimeSeries series)
    {
        for (PaneHandler handler : paneHandlers)
        {
            if (handler == null) continue;  // TODO Remove null check; shouldn't be null when completed

            handler.onTimeSeriesChanged(series);
        }
    }

}
