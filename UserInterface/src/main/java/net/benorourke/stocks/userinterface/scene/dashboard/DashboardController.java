package net.benorourke.stocks.userinterface.scene.dashboard;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.thread.Progress;
import net.benorourke.stocks.framework.thread.TaskDescription;
import net.benorourke.stocks.userinterface.StockApplication;
import net.benorourke.stocks.userinterface.TaskUpdateAdapter;
import net.benorourke.stocks.userinterface.exception.SceneCreationDataException;
import net.benorourke.stocks.userinterface.scene.Controller;
import net.benorourke.stocks.userinterface.scene.SceneHelper;
import net.benorourke.stocks.userinterface.scene.SceneType;
import net.benorourke.stocks.userinterface.scene.dashboard.panes.*;
import net.benorourke.stocks.userinterface.scene.tasks.TasksController;
import net.benorourke.stocks.userinterface.util.Constants;
import net.benorourke.stocks.userinterface.util.FontFamily;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

public class DashboardController extends Controller
{
    private static final double HEADER_SIZE = 24;
    private static final String NAV_BUTTON_STYLE_CLASS = "nav-row";
    private static final String NAV_BUTTON_ICON_STYLE_CLASS = "nav-icon";
    private static final String NAV_BUTTON_TEXT_STYLE_CLASS = "nav-text";
    private static final String NAV_BUTTON_ICON_SELECTED_STYLE_CLASS = "nav-icon-selected";
    private static final String NAV_BUTTON_TEXT_SELECTED_STYLE_CLASS = "nav-text-selected";
    private static final String SERIES_ROW_FXML = "/dashboard-series.fxml";
    public static final String INPUT_FIELD_FXML = "/dashboard-input-field.fxml";

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

    // HOME:
    @FXML private JFXButton homeTestButton;

    // COLLECTION:
    @FXML private JFXComboBox<String> collectionComboBox;
    @FXML private TabPane collectionTabPane;
    //  overview
    @FXML private VBox collectionDataPresentBox;
    //  collect
    @FXML private JFXComboBox collectionCollectSourceComboBox;
    @FXML private JFXDatePicker collectionCollectDataPickerFrom, collectionCollectDataPickerTo;
    @FXML private VBox collectionCollectBox;
    @FXML private JFXButton collectionCollectButton;
    // missing data & duplicates
    @FXML private JFXButton collectionDuplicatesRemove;

    // PRE-PROCESSING:
    @FXML private VBox preprocessingTogglesBox;
    @FXML private JFXComboBox preprocessingPolicyBox;
    @FXML private JFXButton preprocessingBegin;

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
    @FXML HBox tasksRunningBox;
    @FXML FontAwesomeIcon tasksRunningSpinner;
    @FXML private Label tasksRunningLabel;
    @FXML private FontAwesomeIcon shutdownIcon;

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

            row.setOnMouseClicked( event -> selectNavbarBox(row, paneFor));
        }

        model.acquireTimeSeries( () -> updateTimeSeries() );

        // Initialise pane handlers
        paneHandlers = new PaneHandler[DashboardPane.values().length];
        paneHandlers[DashboardPane.HOME.ordinal()] =
                new HomePaneHandler(this, model, homeTestButton);
        paneHandlers[DashboardPane.COLLECTION.ordinal()] =
                new CollectionPaneHandler(this, model, collectionComboBox, collectionTabPane,
                                          collectionDataPresentBox,
                                          collectionCollectSourceComboBox,
                                          collectionCollectDataPickerFrom, collectionCollectDataPickerTo,
                                          collectionCollectBox, collectionCollectButton,
                                         collectionDuplicatesRemove);
        paneHandlers[DashboardPane.PRE_PROCESSING.ordinal()] =
                new PreprocessingHandler(this, model, preprocessingTogglesBox, preprocessingPolicyBox,
                                         preprocessingBegin);
        paneHandlers[DashboardPane.TRAINING.ordinal()] =
                new TrainingPaneHandler(this, model,
                                        trainingHandlerCount, trainingComboBox, trainingFieldBox, trainButton);
        paneHandlers[DashboardPane.EVALUATION.ordinal()] =
                new EvaluationPaneHandler(this, model, evaluationComboBox, evaluationChart);

        for (PaneHandler handler : paneHandlers)
            handler.initialise();

        tasksRunningBox.setOnMouseClicked(event ->
        {
            TasksController.show();
        });

        tasksRunningSpinner.setVisible(false);
        // Create a rotation to constantly rotate the spinner
        RotateTransition spinnerTransition = new RotateTransition(Duration.seconds(2), tasksRunningSpinner);
        spinnerTransition.setFromAngle(0);
        spinnerTransition.setToAngle(360);
        spinnerTransition.setInterpolator(Interpolator.LINEAR);
        spinnerTransition.setCycleCount(Animation.INDEFINITE);
        spinnerTransition.play();
        shutdownIcon.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> System.exit(0));

        StockApplication.registerTaskAdapter((descriptions, progresses) ->
        {
            // Filter out any task types that we chose to ignore (i.e. inflation)
            Predicate<Map.Entry<UUID, TaskDescription>> filter =
                    e -> !Constants.TASKS_TO_IGNORE.contains(e.getValue().getType());
            final int count = (int) descriptions.entrySet().stream()
                                                           .filter(filter)
                                                           .count();
            final String text = count + (count == 1 ? " Task" : " Tasks").concat(" Running");
            StockApplication.runUIThread(() ->
            {
                tasksRunningLabel.setText(text);

                    if (count > 0)
                        tasksRunningSpinner.setVisible(true);
                    else
                        tasksRunningSpinner.setVisible(false);
            });
        });
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

    private void selectNavbarBox(DashboardPane paneFor)
    {
        selectNavbarBox(navRows.get(paneFor.ordinal()), paneFor);
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
        paneVBox.getChildren().clear();
        for (TimeSeries series : model.getTimeSeries())
        {
            SceneHelper.inflateAsync(SERIES_ROW_FXML, result -> {
                if (!result.isSuccess()) return;

                FXMLLoader loader = result.getLoader();
                Parent parent = result.getLoaded();

                Circle circle = (Circle) loader.getNamespace().get("seriesCircle");
                Label name = (Label) loader.getNamespace().get("seriesName");
                Label stock = (Label) loader.getNamespace().get("seriesStock");

                int index = series.getId().hashCode() % 10;
                index = (index < 0) ? index * -1 : index; // Prevent negative indices
                circle.setFill(SERIES_CIRCLE_FILLS[index]);
                name.setText(series.getName());
                stock.setText(series.getStock());

                parent.setOnMouseClicked(event -> changeTimeSeries(series));
                paneVBox.getChildren().add(parent);
            });
        }

        if (model.getTimeSeries().size() > 0)
            changeTimeSeries(model.getTimeSeries().get(0));

    }

    public void changeTimeSeries(TimeSeries series)
    {
        // Don't change it if they're already on it
        if (model.getCurrentlySelectedTimeSeries() != null && model.getCurrentlySelectedTimeSeries().equals(series))
            return;

        model.setCurrentlySelectedTimeSeries(series);

        for (PaneHandler handler : paneHandlers)
        {
            handler.onTimeSeriesChanged(series);
        }

        selectNavbarBox(DashboardPane.HOME);
    }

}
