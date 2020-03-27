package net.benorourke.stocks.framework.model;

import net.benorourke.stocks.framework.model.param.HyperParameter;
import net.benorourke.stocks.framework.model.param.ModelParameters;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;

import java.io.File;
import java.util.List;

public abstract class ModelHandler<T extends PredictionModel>
{
    public abstract T create(ModelParameters configuration);

    /**
     *
     * @return values are the default values for the hyper-parameters
     */
    public abstract List<HyperParameter> getRequiredHyperParameters();

    public abstract void train(T model, ProcessedCorpus corpus);

    public abstract void evaluate(T trainedModel, DataSet data);

    public abstract double[] predictOne(T trainedModel, double[] features);

    public abstract INDArray predict(T trainedModel, INDArray features);

    public abstract boolean writeModel(File file, T trainedModel);

    public abstract T loadModel(File file);

}
