package net.benorourke.stocks.framework.thread.model;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.model.ModelHandler;
import net.benorourke.stocks.framework.model.PredictionModel;
import net.benorourke.stocks.framework.model.ProcessedCorpus;
import net.benorourke.stocks.framework.thread.*;
import net.benorourke.stocks.framework.util.Nullable;

/**
 * No TaskDescription derivative class required as we only want to enable one
 * pre-processing task at a time.
 */
public class TrainingTask implements Task<TaskDescription, Result>
{
    private final ModelHandler modelHandler;
    private final ProcessedCorpus corpus;
    private final long seed;

    private TrainingStage stage;
    private Progress progress;

    @Nullable
    private PredictionModel predictionModel;

    public TrainingTask(ModelHandler modelHandler, ProcessedCorpus corpus, long seed)
    {
        this.modelHandler = modelHandler;
        this.corpus = corpus;
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
        predictionModel = modelHandler.create();
    }

    private void executeTrain()
    {
        modelHandler.train(predictionModel, corpus);
    }

    private void executeEvaluate()
    {
        modelHandler.evaluate(predictionModel, corpus.toDataSet(seed));
    }

    @Override
    public boolean isFinished()
    {
        return stage.equals(TrainingStage.DONE);
    }

    @Override
    public Result getResult()
    {
        return new Result();
    }
}
