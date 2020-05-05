package net.benorourke.stocks.userinterface.scene.dashboard;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.userinterface.scene.Controller;
import net.benorourke.stocks.userinterface.scene.SceneHelper;
import net.benorourke.stocks.userinterface.scene.dashboard.createseries.CreateSeriesController;
import net.benorourke.stocks.userinterface.scene.dashboard.panes.*;
import net.benorourke.stocks.userinterface.util.FontFamily;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DashboardController extends Controller
{
    private static final double HEADER_SIZE = 24;
    private static final String NAV_BUTTON_STYLE_CLASS = "nav-button";
    private static final String NAV_BUTTON_SELECTED_STYLE_CLASS = "nav-button-selected";
    private static final String SERIES_STYLE_CLASS = "series";
    private static final String SERIES_SELECTED_STYLE_CLASS = "series-selected";
    private static final String SERIES_ROW_FXML = "/dashboard-series.fxml";

    public static final String TEXT_INPUT_FIELD_FXML = "/dashboard-text-input-field.fxml";
    public static final String GENERIC_INPUT_FIELD_FXML = "/dashboard-generic-input-field.fxml";
    public static final double GENERIC_INPUT_FIELD_WIDTH_BIND_COEFF = 0.92;

    public static final Color[] PASTEL_FILLS = new Color[]
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

    @FXML private JFXButton createSeriesButton;

    private List<HBox> navRows;

    //////////////////////////////////////////////////////////////////
    //      PANE
    //////////////////////////////////////////////////////////////////
    @FXML private TabPane tabPane;

    //////////////////////////////////////////////////////////////////
    //      PANE HANDLERS
    //////////////////////////////////////////////////////////////////
    private PaneHandler[] paneHandlers;

    // OVERVIEW:
    @FXML private JFXComboBox<String> overviewComboBox;
    @FXML private TabPane overviewTabPane;
    //  overview
    @FXML private VBox overviewDataPresentBox;
    @FXML private PieChart overviewDistributionChart;
    // missing data & duplicates
    @FXML private JFXButton overviewDuplicatesRemoveButton;
    // danger zone
    @FXML private JFXButton overviewDeleteButton;

    // COLLECTION:
    @FXML private JFXComboBox collectionCollectSourceComboBox;
    @FXML private JFXDatePicker collectionCollectDataPickerFrom, collectionCollectDataPickerTo;
    @FXML private VBox collectionCollectBox;
    @FXML private JFXButton collectionCollectButton;

    // INJECTION:
    @FXML private JFXComboBox injectionSourceComboBox;
    @FXML private JFXDatePicker injectionDatePicker;
    @FXML private VBox injectionDataBox;
    @FXML private JFXButton injectionInjectButton;

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
    //      BOTTOM BAR
    //////////////////////////////////////////////////////////////////
    @FXML HBox tasksRunningBox;
    @FXML FontAwesomeIcon tasksRunningSpinner;
    @FXML private Label tasksRunningLabel;
    private BottomBarHelper bottomBarHelper;

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
        navRows.addAll(resolveNavBarButtons(root));
        for (int i = 0; i < navRows.size(); i ++)
        {
            HBox row = navRows.get(i);
            DashboardPane paneFor = DashboardPane.values()[i];

            row.setOnMouseClicked( event -> selectNavbarButton(row, paneFor));
        }
        createSeriesButton.setOnMouseClicked(e -> CreateSeriesController.show(this));

        // Acquire all of the present timeserise
        model.acquireTimeSeries( () -> updateTimeSeries() );

        // Instantiate and initialise pane handlers
        paneHandlers = new PaneHandler[DashboardPane.values().length];
        paneHandlers[DashboardPane.OVERVIEW.ordinal()] =
                new OverviewPaneHandler(this, model, overviewComboBox, overviewTabPane,
                                        overviewDataPresentBox, overviewDistributionChart,
                                        overviewDuplicatesRemoveButton, overviewDeleteButton);
        paneHandlers[DashboardPane.COLLECTION.ordinal()] =
                new CollectionPaneHandler(this, model, collectionCollectSourceComboBox,
                                          collectionCollectDataPickerFrom, collectionCollectDataPickerTo,
                                          collectionCollectBox, collectionCollectButton);
        paneHandlers[DashboardPane.INJECTION.ordinal()] =
                new InjectionPaneHandler(this, model, injectionSourceComboBox, injectionDatePicker,
                                         injectionDataBox, injectionInjectButton);
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

        // Create relevant listeners & animations for the bottom bar
        bottomBarHelper = new BottomBarHelper(tasksRunningBox, tasksRunningSpinner, tasksRunningLabel);
        bottomBarHelper.initialise();
    }

    private List<HBox> resolveNavBarButtons(Parent parent)
    {
        List<HBox> boxes = new ArrayList<>();
        for (Node child : parent.getChildrenUnmodifiable())
        {
            // Recurse the function to get all nav bar boxes contained within this child
            if (child instanceof Parent) boxes.addAll(resolveNavBarButtons((Parent) child));

            boolean isNavButton = child.getStyleClass().contains(NAV_BUTTON_STYLE_CLASS)
                                        || child.getStyleClass().contains(NAV_BUTTON_SELECTED_STYLE_CLASS);
            if (child instanceof HBox && isNavButton)
                boxes.add((HBox) child);
        }
        return boxes;
    }

    private void selectNavbarButton(HBox toSelect, DashboardPane paneFor)
    {
        // Check if they've selected a TimeSeries - reject otherwise
        if (model.getCurrentlySelectedTimeSeries() == null && !paneFor.equals(DashboardPane.OVERVIEW))
        {
            snackbarNullTimeSeries();
            return;
        }

        // Determine whether they can navigate to this navigation pane based on the current FlowStage of the TimeSeries
        FlowStage current = model.getCurrentFlowStage();
        PaneHandler handler = paneHandlers[paneFor.ordinal()];
        if (handler.getNavigationRequirement() != null && handler.getNavigationRequirement().isBefore(current))
        {
            snackbar(SnackbarType.NAVIGATION_RESTRICTION, handler.getNavigationRequirement().getErrorMessage());
            return;
        }

        SingleSelectionModel<Tab> model = tabPane.getSelectionModel();
        model.select(paneFor.ordinal());

        for (HBox row : navRows)
        {
            row.getStyleClass().clear();
            row.getStyleClass().add(toSelect.equals(row) ? NAV_BUTTON_SELECTED_STYLE_CLASS : NAV_BUTTON_STYLE_CLASS);
        }
    }

    public void selectNavbarButton(DashboardPane paneFor)
    {
        selectNavbarButton(navRows.get(paneFor.ordinal()), paneFor);
    }

    public void updateTimeSeries()
    {
        paneVBox.getChildren().clear();
        for (TimeSeries series : model.getTimeSeries())
            inflateTimeSeries(series, false);
    }

    /**
     *
     * @param series
     * @param select whether to select this series once it has been inflated
     */
    public void inflateTimeSeries(TimeSeries series, final boolean select)
    {
        SceneHelper.inflateAsync(SERIES_ROW_FXML, result ->
        {
            if (!result.isSuccess()) return;

            FXMLLoader loader = result.getLoader();
            Parent parent = result.getLoaded();

            Circle circle = (Circle) loader.getNamespace().get("seriesCircle");
            Label name = (Label) loader.getNamespace().get("seriesName");
            Label stock = (Label) loader.getNamespace().get("seriesStock");

            int index = series.getId().hashCode() % 10;
            index = (index < 0) ? index * -1 : index; // Prevent negative indices
            circle.setFill(PASTEL_FILLS[index]);
            name.setText(series.getName());
            stock.setText(series.getStock());

            parent.setOnMouseClicked(event -> changeTimeSeries(series, parent));
            paneVBox.getChildren().add(parent);

            // Add the component and then sort all of the TimeSeries so they appear to be in order:

            // Have to clone the collection, otherwise the regular sorting algorithms will attempt to add duplicate
            // components; throwing an exception
            ObservableList<Node> clonedCollection = FXCollections.observableArrayList(paneVBox.getChildren());
            Collections.sort(clonedCollection, Comparator.comparing(child ->
            {
                // Structure is as follows:
                // - HBox (child here)
                //   - Circle
                //   - VBox
                //      - Label (series name - this is what we want)
                //      - Label (stock info)

                HBox hbox = (HBox) child;
                VBox vbox = (VBox) hbox.getChildren().get(1);
                Label nameLabel = (Label) vbox.getChildren().get(0);
                return nameLabel.getText().toLowerCase();
            }));
            paneVBox.getChildren().setAll(clonedCollection);

            if (select)
                changeTimeSeries(series, parent);
        });
    }

    public void changeTimeSeries(TimeSeries series, Node parent)
    {
        // Don't change it if they're already on it
        if (model.getCurrentlySelectedTimeSeries() != null && model.getCurrentlySelectedTimeSeries().equals(series))
            return;

        model.setCurrentlySelectedTimeSeries(series);

        // Resolve the FlowStage to prevent them switching navigation tabs if they shouldn't be able to
        model.setCurrentFlowStage(FlowStage.defaultStage());
        model.resolveFlowStage(series, () -> {});

        selectTimeSeries(parent);
        selectNavbarButton(DashboardPane.OVERVIEW);

        for (PaneHandler handler : paneHandlers)
            handler.onTimeSeriesChanged(series);
    }

    private void selectTimeSeries(Node parent)
    {
        for (Node node : paneVBox.getChildren())
        {
            node.getStyleClass().clear();
            node.getStyleClass().add(parent.equals(node) ? SERIES_SELECTED_STYLE_CLASS : SERIES_STYLE_CLASS);
        }
    }

    public PaneHandler getPaneHandler(DashboardPane pane)
    {
        return paneHandlers[pane.ordinal()];
    }

}
