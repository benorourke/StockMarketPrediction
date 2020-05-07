package net.benorourke.stocks.userinterface.scene.dashboard.panes;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.model.ModelHandler;
import net.benorourke.stocks.framework.model.ModelHandlerManager;
import net.benorourke.stocks.framework.model.ProcessedDataset;
import net.benorourke.stocks.framework.model.param.HyperParameter;
import net.benorourke.stocks.framework.model.param.ModelParameters;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.util.Nullable;
import net.benorourke.stocks.userinterface.scene.Controller;
import net.benorourke.stocks.userinterface.scene.SceneHelper;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardController;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardModel;
import net.benorourke.stocks.userinterface.scene.dashboard.FlowStage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.benorourke.stocks.userinterface.StockApplication.runBgThread;
import static net.benorourke.stocks.userinterface.StockApplication.runUIThread;

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

            final List<ModelHandlerManager.RuntimeCreator> creatorsClone =
                    new ArrayList<>(model.getModelHandlerCreators());
            final List<String> creatorNames = creatorsClone
                                                    .stream()
                                                    .map(c -> c.name())
                                                    .collect(Collectors.toList());
            selectHandler.getItems().addAll(creatorNames);
            selectHandler.valueProperty().addListener((observable, oldValue, newValue) ->
            {
                ModelHandlerManager.RuntimeCreator creator = creatorsClone.get(creatorNames.indexOf(newValue));
                model.setCurrentlySelectedModelHandlerCreator(creator);
                populateTrainBox(creator);
            });
        });

        trainButton.setOnMouseClicked(event -> onTrainClicked());
    }

    @Override
    public void onTimeSeriesChanged(TimeSeries series) { }

    @Override
    public void onSwitchedTo()
    {
        // We can guarantee here that there's a) a processed dataset and b) a time series selected
        selectHandler.getSelectionModel().select(0);
    }

    @Override
    public FlowStage getNavigationRequirement()
    {
        return FlowStage.PRE_PROCESSED;
    }

    /**
     * {@link #inputParents} along with {@link #inputFields} will be set to null, followed by each parent being
     * asynchronously inflated (on differing threads).
     *
     * Each element in {@link #inputFields} will be NOT NULL when the box has been fully generated.
     *
     * @param creator
     */
    public void populateTrainBox(ModelHandlerManager.RuntimeCreator creator)
    {
        // + 1 for the name input field
        int length = 1 + creator.getRequiredParameters().size();
        inputParents = new Parent[length];
        inputFields = new JFXTextField[length];

        final TimeSeries series = model.getCurrentlySelectedTimeSeries();
        runBgThread(framework ->
        {
            File processedFile = framework.getFileManager().getProcessedCorpusFile(series);
            final ProcessedDataset processed =
                    framework.getFileManager().loadJson(processedFile, ProcessedDataset.class).get();

            runUIThread(() ->
            {
                if (processed == null)
                {
                    controller.snackbar(Controller.SnackbarType.ERROR, "Unable to load dataset");
                    return;
                }

                // So that we can have the correct number of inputs, outputs, etc.
                ModelHandler instance = creator.createFromDataset(processed);
                ModelParameters config = instance.getConfiguration();

                inflateInputFieldAsync(0, "Model Name", "Enter Name", null, false);
                for (int i = 0; i < creator.getRequiredParameters().size(); i ++)
                {
                    HyperParameter param = (HyperParameter) creator.getRequiredParameters().get(i);
                    int configValue = config.get(param.getName()); // use whatever is stored in the parameters config
                                                                   // as the default; since this changes based on num
                                                                   // of features
                    inflateInputFieldAsync(i + 1, param.getName(),
                                 null, String.valueOf(configValue),
                                           !param.isSelfGenerated());

                    Framework.debug("Setting jfx field val " + param.getName() + " to " + configValue);
                }
            });

        });
    }

    private void inflateInputFieldAsync(int index, String fieldName,
                                        @Nullable String promptText, @Nullable String text,
                                        boolean disableInput)
    {
        SceneHelper.inflateAsync(DashboardController.TEXT_INPUT_FIELD_FXML, result ->
        {
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
        final TimeSeries series = model.getCurrentlySelectedTimeSeries();
        if(series == null)
        {
            controller.snackbarNullTimeSeries();
            return;
        }

        // Check to ensure all the text fields have been asynchronously inflate before we even consider
        if(!allInputFieldsLoaded())
        {
            controller.snackbar(Controller.SnackbarType.ERROR, "Please wait for input fields to laod.");
            return;
        }

        String modelName = inputFields[0].getText();
        if (modelName.length() == 0)
        {
            controller.snackbar(Controller.SnackbarType.ERROR, "You must specify a name for this model!");
            return;
        }

        // TODO - Check if model exists

        ModelHandlerManager.RuntimeCreator creator = model.getCurrentlySelectedModelHandlerCreator();
        // We could create from either parameters or dataset here, but we'll use parameters since the dynamic
        // values such as number of model inputs/outputs are already stored within the FX input fields.
        //
        // The ModelHandler should have also populated the parameters object with missing default values;
        // which we can change.
        ModelParameters configuration = new ModelParameters();
        ModelHandler instance = creator.createFromParameters(configuration);
        // Now to modify all the parameters based on the FX input fields
        for (int i = 0; i < creator.getRequiredParameters().size(); i ++)
        {
            HyperParameter param = (HyperParameter) creator.getRequiredParameters().get(i);
            String strParameterValue = inputFields[i + 1].getText();

            try
            {
                int parameterValue = Integer.valueOf(strParameterValue);
                configuration.set(param.getName(), parameterValue);
            }
            catch (NumberFormatException ignored)
            {
                controller.snackbar(Controller.SnackbarType.ERROR,
                            "Unable to parse parameter to an integer: " + param.getName());
                return;
            }
        }

        // We can now use our ModelHandler to train the model
    }

    public boolean allInputFieldsLoaded()
    {
        for (JFXTextField input : inputFields)
            if (input == null) return false;

        return true;
    }

}
