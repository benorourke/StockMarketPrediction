package net.benorourke.stocks.framework.thread.model;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.model.ModelHandler;
import net.benorourke.stocks.framework.model.param.ModelParameters;
import net.benorourke.stocks.framework.model.PredictionModel;
import net.benorourke.stocks.framework.model.ProcessedCorpus;
import net.benorourke.stocks.framework.thread.*;
import net.benorourke.stocks.framework.util.Nullable;

/**
 * No TaskDescription derivative class required as we only want to enable one
 * pre-processing task at a time.
 */
public class TrainingTask<T extends PredictionModel> implements Task<TaskDescription, TrainingResult<T>>
{
    private final ModelHandler<T> modelHandler;
    private final ModelParameters modelParameters;
    private final ProcessedCorpus training, testing;
    private final long seed;

    private TrainingStage stage;
    private Progress progress;

    @Nullable
    private T predictionModel;

    public TrainingTask(ModelHandler<T> modelHandler, ModelParameters modelParameters,
                        ProcessedCorpus training, ProcessedCorpus testing, long seed)
    {
        this.modelHandler = modelHandler;
        this.modelParameters = modelParameters;
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
        return progress = new Progress();
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
        predictionModel = modelHandler.create(modelParameters);
    }

    private void executeTrain()
    {
        modelHandler.train(predictionModel, training);
    }

    private void executeEvaluate()
    {
        modelHandler.evaluate(predictionModel, testing.toDataSet(seed));
    }

    @Override
    public boolean isFinished()
    {
        return stage.equals(TrainingStage.DONE);
    }

    @Override
    public TrainingResult getResult()
    {
        return new TrainingResult(predictionModel);
    }

}
