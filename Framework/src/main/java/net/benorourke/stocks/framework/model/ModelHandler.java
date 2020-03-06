package net.benorourke.stocks.framework.model;

public abstract class ModelHandler<T extends PredictionModel>
{
    private final long seed;

    public ModelHandler(long seed)
    {
        this.seed = seed;
    }

    public ModelHandler()
    {
        this(0);
    }

    public abstract T create();

    public abstract void train(T model, ProcessedCorpus corpus);

    public abstract void evaluate(T trainedModel);

    public long getSeed()
    {
        return seed;
    }

}
