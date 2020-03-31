package net.benorourke.stocks.framework.thread.model;

import net.benorourke.stocks.framework.model.ModelEvaluation;
import net.benorourke.stocks.framework.model.PredictionModel;
import net.benorourke.stocks.framework.thread.Result;

public class TrainingResult<T extends PredictionModel> extends Result
{
    private final T trainedModel;
    private final ModelEvaluation evaluation;

    public TrainingResult(T trainedModel, ModelEvaluation evaluation)
    {
        this.trainedModel = trainedModel;
        this.evaluation = evaluation;
    }

    public T getTrainedModel()
    {
        return trainedModel;
    }

    public ModelEvaluation getEvaluation()
    {
        return evaluation;
    }

}
