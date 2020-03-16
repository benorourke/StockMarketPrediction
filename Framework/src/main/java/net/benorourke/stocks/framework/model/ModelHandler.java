package net.benorourke.stocks.framework.model;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;

import java.io.File;

public abstract class ModelHandler<T extends PredictionModel>
{
    private final long seed;

    public ModelHandler(long seed)
    {
        this.seed = seed;
    }

    public abstract T create();

    public abstract void train(T model, ProcessedCorpus corpus);

    public abstract void evaluate(T trainedModel, DataSet data);

    public abstract double[] predictOne(T trainedModel, double[] features);

    public abstract INDArray predict(T trainedModel, INDArray features);

    public abstract boolean writeModel(File file, T trainedModel);

    public abstract T loadModel(File file);

    public long getSeed()
    {
        return seed;
    }

}
