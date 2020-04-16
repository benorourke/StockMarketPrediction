package net.benorourke.stocks.userinterface.scene.dashboard.panes;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import net.benorourke.stocks.framework.model.ModelHandlerManager;
import net.benorourke.stocks.framework.model.param.HyperParameter;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.util.Nullable;
import net.benorourke.stocks.userinterface.StockApplication;
import net.benorourke.stocks.userinterface.scene.Controller;
import net.benorourke.stocks.userinterface.scene.SceneHelper;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardController;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardModel;

import java.util.stream.Collectors;

public class TrainingPaneHandler extends PaneHandler
{
    private final Label modelHandlers;
    private final JFXComboBox<String> selectHandler;
    private final VBox trainingFieldBox;
    private final JFXButton trainButton;

    @Nullable
    private Parent[] inputParents;
    /** Index 0 is always for the name of the model */
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
            generateTrainBox(model.getModelHandlerCreators().get(selection));
        });

        trainButton.setOnMouseClicked(event -> onTrainClicked());
    }

    @Override
    public void onTimeSeriesChanged(TimeSeries series)
    {
        // TODO
        StockApplication.debug("Verändert zum " + series.getName());
    }

    /**
     * {@link #inputParents} along with {@link #inputFields} will be set to null, followed by each parent being
     * asynchronously inflated (on differing threads).
     *
     * Each element in {@link #inputFields} will be NOT NULL when the box has been fully generated.
     *
     * @param creator
     */
    public void generateTrainBox(ModelHandlerManager.RuntimeCreator creator)
    {
        // + 1 for the name input field
        int length = 1 + creator.getRequiredParameters().size();
        inputParents = new Parent[length];
        inputFields = new JFXTextField[length];

        inflateInputFieldAsync(0, "Model Name", "Enter Name", null, false);
        for (int i = 0; i < creator.getRequiredParameters().size(); i ++)
        {
            HyperParameter param = (HyperParameter) creator.getRequiredParameters().get(i);
            inflateInputFieldAsync(i + 1, param.getName(),
                         null, String.valueOf(param.getDefaultValue()),
                                   !param.isSelfGenerated());
        }
    }

    private void inflateInputFieldAsync(int index, String fieldName,
                                        @Nullable String promptText, @Nullable String text,
                                        boolean disableInput)
    {
        SceneHelper.inflateAsync(DashboardController.INPUT_FIELD_FXML, result -> {
            if (!result.isSuccess()) return;

            FXMLLoader loader = result.getLoader();
            Parent parent = result.getLoaded();

            Label label = (Label) loader.getNamespace().get("valueLabel");
            label.setText(fieldName);

            JFXTextField field = (JFXTextField) loader.getNamespace().get("textField");
            if (promptText != null)
                field.setPromptText(promptText);
            if (text != null)
                field.setText(text);
            field.setDisable(disableInput);

            inputParents[index] = parent;
            inputFields[index] = field;

            // Async threads can return the results in differing order, so instead of directly adding the parent
            // to the trainingFieldBox we'll wait until they've all been returned so that the order remains consistent
            if (allInputFieldsLoaded())
            {
                trainingFieldBox.getChildren().clear();
                for (Parent elem : inputParents)
                    trainingFieldBox.getChildren().add(elem);
            }
        });
    }

    public void onTrainClicked()
    {
        // Check to ensure all the text fields have been asynchronously inflate before we even consider
        if(!allInputFieldsLoaded()) return;

        controller.snackbar(Controller.SnackbarType.ERROR, "Test");

        // TODO - Once trained update the EvaluationPaneHandler
    }

    public boolean allInputFieldsLoaded()
    {
        for (JFXTextField input : inputFields)
            if (input == null) return false;

        return true;
    }

}
