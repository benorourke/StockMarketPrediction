package net.benorourke.stocks.framework.thread.model;

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
