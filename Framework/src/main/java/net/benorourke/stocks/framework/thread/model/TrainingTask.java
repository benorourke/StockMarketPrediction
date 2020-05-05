package net.benorourke.stocks.framework.thread.model;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.model.ModelEvaluation;
import net.benorourke.stocks.framework.model.ModelHandler;
import net.benorourke.stocks.framework.model.param.ModelParameters;
import net.benorourke.stocks.framework.model.PredictionModel;
import net.benorourke.stocks.framework.model.ProcessedDataset;
import net.benorourke.stocks.framework.thread.*;
import net.benorourke.stocks.framework.util.DateUtil;
import net.benorourke.stocks.framework.util.Nullable;
import net.benorourke.stocks.framework.util.Tuple;

import java.util.*;

/**
 * No TaskDescription derivative class required as we only want to enable one
 * pre-processing task at a time.
 */
public class TrainingTask<T extends PredictionModel> implements Task<TaskDescription, TrainingResult<T>>
{
    private static final LinkedHashMap<Integer, Double> PROGRESS_STEPS;
    private static final int PROGRESS_CREATE = 0;
    private static final int PROGRESS_TRAIN = 1;
    private static final int PROGRESS_EVALUATE = 2;
    private static final int PROGRESS_EVALUATE_SORT = 3;

    static
    {
        PROGRESS_STEPS = new LinkedHashMap<>();
        PROGRESS_STEPS.put(PROGRESS_CREATE, 20.0D);
        PROGRESS_STEPS.put(PROGRESS_TRAIN, 60.0D);
        PROGRESS_STEPS.put(PROGRESS_EVALUATE, 10.0D);
        PROGRESS_STEPS.put(PROGRESS_EVALUATE_SORT, 10.0D);
    }

    private final ModelHandler<T> modelHandler;
    private final ProcessedDataset training, testing;
    private final long seed; // TODO - Do something with this

    private TrainingStage stage;
    private Progress progress;
    private Progress.Helper progressHelper;

    @Nullable
    private T predictionModel;
    @Nullable
    private ModelEvaluation evaluation;

    public TrainingTask(ModelHandler<T> modelHandler, ProcessedDataset training, ProcessedDataset testing, long seed)
    {
        this.modelHandler = modelHandler;
        this.training = training;
        this.testing = testing;
        this.seed = seed;

        stage = TrainingStage.first();
    }

    @Override
    public TaskType getType()
    {
        return TaskType.TRAINING;
    }

    @Override
    public TaskDescription getDescription()
    {
        return new TaskDescription(TaskType.TRAINING)
        {
            @Override
            public boolean equals(Object object)
            {
                return object instanceof TaskDescription
                        && ((TaskDescription) object).getType().equals(getType());
            }
        };
    }

    @Override
    public Progress createTaskProgress()
    {
        progress = new Progress();
        progressHelper = new Progress.Helper(progress, PROGRESS_STEPS);
        return progress;
    }

    @Override
    public void run()
    {
        Framework.info("Executing stage " + stage.toString());
        if (executeStage())
            stage = stage.next();
    }

    private boolean executeStage()
    {
        switch (stage)
        {
            case CREATE:
                executeCreate();
                return true;
            case TRAIN:
                executeTrain();
                return true;
            case EVALUATE:
                executeEvaluate();
                return true;
            case DONE:
                break;
        }
        return false;
    }

    private void executeCreate()
    {
        predictionModel = modelHandler.create();
        progressHelper.updatePercentage(PROGRESS_CREATE, 100.0);
    }

    private void executeTrain()
    {
        modelHandler.train(predictionModel, training);
        progressHelper.updatePercentage(PROGRESS_TRAIN, 100.0);
    }

    private void executeEvaluate()
    {
        evaluation = modelHandler.evaluate(predictionModel, training, testing);

        List<ModelEvaluation.Prediction> predictions = new ArrayList();
        predictions.addAll(evaluation.getTrainingPredictions());
        predictions.addAll(evaluation.getTestingPredictions());
        progressHelper.updatePercentage(PROGRESS_EVALUATE, 100.0);

        Collections.sort(predictions, Comparator.comparing(ModelEvaluation.Prediction::getDate));
        progressHelper.updatePercentage(PROGRESS_EVALUATE_SORT, 100.0);
    }

    @Override
    public boolean isFinished()
    {
        return stage.equals(TrainingStage.DONE);
    }

    @Override
    public TrainingResult getResult()
    {
        return new TrainingResult(predictionModel, evaluation);
    }

}
