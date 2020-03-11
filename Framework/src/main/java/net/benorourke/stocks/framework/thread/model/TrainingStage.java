package net.benorourke.stocks.framework.thread.model;

import net.benorourke.stocks.framework.thread.preprocessing.PreprocessingStage;

public enum TrainingStage
{
    CREATE,
    TRAIN,
    EVALUATE,
    DONE;

    public static TrainingStage first()
    {
        return CREATE;
    }

    public TrainingStage next()
    {
        switch (this)
        {
            case CREATE:
                return TRAIN;
            case TRAIN:
                return EVALUATE;
            case EVALUATE:
                return DONE;

            case DONE:
                return DONE;
            default:
                return null;
        }
    }

}
