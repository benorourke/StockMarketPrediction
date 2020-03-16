package net.benorourke.stocks.framework.thread.model;

import net.benorourke.stocks.framework.model.PredictionModel;
import net.benorourke.stocks.framework.thread.Result;

public class TrainingResult<T extends PredictionModel> extends Result
{
    private final T trainedModel;

    public TrainingResult(T trainedModel)
    {
        this.trainedModel = trainedModel;
    }

    public T getTrainedModel()
    {
        return trainedModel;
    }

}
