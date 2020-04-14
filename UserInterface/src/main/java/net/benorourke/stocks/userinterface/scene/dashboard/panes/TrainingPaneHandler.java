package net.benorourke.stocks.userinterface.scene.dashboard.panes;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.model.ModelHandlerManager;
import net.benorourke.stocks.framework.model.param.HyperParameter;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.util.Nullable;
import net.benorourke.stocks.framework.util.Tuple;
import net.benorourke.stocks.userinterface.StockApplication;
import net.benorourke.stocks.userinterface.exception.InflationException;
import net.benorourke.stocks.userinterface.scene.Controller;
import net.benorourke.stocks.userinterface.scene.SceneHelper;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardController;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardModel;

import java.util.stream.Collectors;

public class TrainingPaneHandler extends PaneHandler
{
    private static final String TRAINING_FIELD_FXML = "/dashboard-training-field.fxml";

    private final Label modelHandlers;
    private final JFXComboBox<String> selectHandler;
    private final VBox trainingFieldBox;
    private final JFXButton trainButton;

    /**
     * Index 0 is always for the name of the model
     */
    @Nullable
    private JFXTextField[] inputFields;

    public TrainingPaneHandler(DashboardController controller, DashboardModel model,
                               Label modelHandlers, JFXComboBox<String> selectHandler,
                               VBox trainingFieldBox, JFXButton trainButton)
    {
        super(controller, model);

        this.modelHandlers = modelHandlers;
        this.selectHandler = selectHandler;
        this.trainingFieldBox = trainingFieldBox;
        this.trainButton = trainButton;
    }

    @Override
    public void initialise()
    {
        // Load the model handlers & set the text once the value is retrieved
        model.acquireModelHandlers(() ->
        {
            modelHandlers.setText(String.valueOf(model.getModelHandlerCreators().size()));

            selectHandler.getItems().clear();
            selectHandler.getItems().addAll(model.getModelHandlerCreators()
                                                    .stream().map(c -> c.name())
                                                    .collect(Collectors.toList()));

            // We can always assume there will be at least one - the Feed Forward Neural Network Handler
            selectHandler.getSelectionModel().select(0);

            int selection = selectHandler.getSelectionModel().getSelectedIndex();
            setTrainBox(model.getModelHandlerCreators().get(selection));
        });

        trainButton.setOnMouseClicked(event -> onTrainClicked());
    }

    @Override
    public void onTimeSeriesChanged(TimeSeries series)
    {
        // TODO
        StockApplication.debug("Verändert zum " + series.getName());
    }

    public void setTrainBox(ModelHandlerManager.RuntimeCreator creator)
    {
        trainingFieldBox.getChildren().clear();
        // + 1 for the name
        inputFields = new JFXTextField[1 + creator.getRequiredParameters().size()];

        inputFields[0] = generateInputField("Model Name", "Enter Name");
        for (int i = 0; i < creator.getRequiredParameters().size(); i ++)
        {
            HyperParameter param = (HyperParameter) creator.getRequiredParameters().get(i);
            inputFields[i + 1] = generateInputField(param.getName(), "");

            if (!param.isSelfGenerated())
                inputFields[i + 1].setDisable(true);

            inputFields[i + 1].setText(String.valueOf(param.getDefaultValue()));
        }
    }

    private JFXTextField generateInputField(String fieldName, String promptText)
    {
        try
        {
            Tuple<FXMLLoader, Parent> tuple = SceneHelper.inflate(TRAINING_FIELD_FXML);
            FXMLLoader loader = tuple.getA();
            Parent parent = tuple.getB();

            Label label = (Label) loader.getNamespace().get("valueLabel");
            label.setText(fieldName);

            JFXTextField field = (JFXTextField) loader.getNamespace().get("textField");
            field.setPromptText(promptText);

            trainingFieldBox.getChildren().add(parent);
            return field;
        }
        catch (InflationException e)
        {
            Framework.error("Unable to inflate " + TRAINING_FIELD_FXML, e);
            return null;
        }
    }

    public void onTrainClicked()
    {
        if (inputFields == null)
            return;

        controller.snackbar(Controller.SnackbarType.ERROR, "Test");

        // TODO - Once trained update the EvaluationPaneHandler
    }

}
