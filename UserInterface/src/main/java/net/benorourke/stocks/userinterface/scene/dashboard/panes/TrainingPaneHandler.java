package net.benorourke.stocks.userinterface.scene.dashboard.panes;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.exception.TaskStartException;
import net.benorourke.stocks.framework.model.ModelHandler;
import net.benorourke.stocks.framework.model.ModelHandlerManager;
import net.benorourke.stocks.framework.model.PredictionModel;
import net.benorourke.stocks.framework.model.ProcessedDataset;
import net.benorourke.stocks.framework.model.param.HyperParameter;
import net.benorourke.stocks.framework.model.param.ModelParameters;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.thread.model.TrainingTask;
import net.benorourke.stocks.framework.util.Nullable;
import net.benorourke.stocks.framework.util.Tuple;
import net.benorourke.stocks.userinterface.scene.Controller;
import net.benorourke.stocks.userinterface.scene.SceneHelper;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardController;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardModel;
import net.benorourke.stocks.userinterface.scene.dashboard.FlowStage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static net.benorourke.stocks.userinterface.StockApplication.runBgThread;
import static net.benorourke.stocks.userinterface.StockApplication.runUIThread;

public class TrainingPaneHandler extends PaneHandler
{
    private final Label modelHandlers;
    private final JFXComboBox<String> selectHandler;
    private final JFXTextField trainTrainingPercentage, trainTestingPercentage;
    private final JFXTextField trainSeed;
    private final VBox trainingFieldBox;
    private final JFXButton trainButton;

    @Nullable
    private Parent[] inputParents;
    /** Index 0 is always for the name of the model */
    @Nullable
    private JFXTextField[] inputFields;

    public TrainingPaneHandler(DashboardController controller, DashboardModel model,
                               Label modelHandlers, JFXComboBox<String> selectHandler,
                               JFXTextField trainTrainingPercentage, JFXTextField trainTestingPercentage,
                               JFXTextField trainSeed, VBox trainingFieldBox, JFXButton trainButton)
    {
        super(controller, model);

        this.modelHandlers = modelHandlers;
        this.selectHandler = selectHandler;
        this.trainTrainingPercentage = trainTrainingPercentage;
        this.trainTestingPercentage = trainTestingPercentage;
        this.trainSeed = trainSeed;
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
                    inflateInputFieldAsync(i + 1, param.getName(), null, String.valueOf(configValue),
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

        ModelHandlerManager.RuntimeCreator creator = model.getCurrentlySelectedModelHandlerCreator();
        // We could create from either parameters or dataset here, but we'll use parameters since the dynamic
        // values such as number of model inputs/outputs are already stored within the FX input fields.
        //
        // The ModelHandler should have also populated the parameters object with missing default values;
        // which we can change.
        ModelParameters configuration = new ModelParameters();
        ModelHandler handlerInstance = creator.createFromParameters(configuration);
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
        Tuple<Boolean, Double> splitAt = getSplitAt();
        if (!splitAt.getA()) return;

        try
        {
            long seed = Long.parseLong(trainSeed.getText());
            beginTraining(series, handlerInstance, modelName, splitAt.getB(), seed);
        }
        catch (NumberFormatException ignored)
        {
            controller.snackbar(Controller.SnackbarType.ERROR, "Unable to parse random seed as long");
        }
    }

    private void beginTraining(TimeSeries series, ModelHandler handler, String name, double splitAt, long seed)
    {
        runBgThread(framework ->
        {
            if (framework.getFileManager().getModelFile(series, name).exists())
            {
                runUIThread(() -> controller.snackbar(Controller.SnackbarType.ERROR,
                                              "A model by that name already exists!"));
                return;
            }

            File processedFile = framework.getFileManager().getProcessedCorpusFile(series);
            ProcessedDataset dataset = framework.getFileManager().loadJson(processedFile, ProcessedDataset.class).get();
            if (dataset == null)
            {
                runUIThread(() -> controller.snackbar(Controller.SnackbarType.ERROR,
                                              "Unable to load processed dataset."));
                return;
            }

            dataset.shuffle(seed);
            List<ProcessedDataset> split = dataset.split(splitAt);

            if (split.get(0).size() == 0)
            {
                runUIThread(() -> controller.snackbar(Controller.SnackbarType.INFO,
                                              "Insufficient Training Data (0): adjust your split ratio"));
                return;
            }
            if (split.get(1).size() == 0)
            {
                runUIThread(() -> controller.snackbar(Controller.SnackbarType.INFO,
                                              "Insufficient Testing Data (0): adjust your split ratio"));
                return;
            }

            TrainingTask<PredictionModel> task = new TrainingTask(handler, split.get(0), split.get(1), seed);

            runUIThread(() -> controller.snackbar(Controller.SnackbarType.INFO, "Beginning Training"));
            try
            {
                framework.getTaskManager().scheduleRepeating(task, result ->
                {
                    File modelFile = framework.getFileManager().getModelFile(series, name);
                    File evalFile = framework.getFileManager().getModelEvaluationFile(series, name);

                    handler.writeModel(modelFile, result.getTrainedModel());
                    framework.getFileManager().writeJson(evalFile, result.getEvaluation());

                    runUIThread(() ->
                    {
                        // Resolve the flow stage so if they were to click evaluate models they'd be able to
                        model.resolveFlowStage(series, () -> {});

                        // Update the evaluation pane
                        EvaluationPaneHandler evalPane =
                                (EvaluationPaneHandler) controller.getPaneHandler(DashboardPane.EVALUATION);
                        evalPane.onModelsChanged(series);

                        controller.snackbar(Controller.SnackbarType.INFO, "Model Saved");
                    });

                }, 200, 1000, TimeUnit.MILLISECONDS);
            }
            catch (TaskStartException e)
            {
                e.printStackTrace();
                runUIThread(() -> controller.snackbar(Controller.SnackbarType.ERROR, e.getMessage()));
            }
        });
    }

    /**
     * Will show relevant error snackbars.
     *
     * @return A: whether the input fields were valid, B: the percentage: -1.0 if invalid
     */
    public Tuple<Boolean, Double> getSplitAt()
    {
        try
        {
            int train = Integer.parseInt(trainTrainingPercentage.getText());
            int test = Integer.parseInt(trainTestingPercentage.getText());
            if (train + test != 100)
            {
                controller.snackbar(Controller.SnackbarType.ERROR, "Training + Testing must equal 100");
                return new Tuple<>(false, -1.0);
            }

            return new Tuple<>(true, (double) train / 100.0D);
        }
        catch (NumberFormatException ignored)
        {
            controller.snackbar(Controller.SnackbarType.ERROR, "Unable to parse train/testing split ratio as ints");
            return new Tuple<>(false, -1.0);
        }
    }

    public boolean allInputFieldsLoaded()
    {
        for (JFXTextField input : inputFields)
            if (input == null) return false;

        return true;
    }

}
