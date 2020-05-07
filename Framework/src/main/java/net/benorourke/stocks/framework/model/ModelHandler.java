package net.benorourke.stocks.framework.model;

import net.benorourke.stocks.framework.model.param.HyperParameter;
import net.benorourke.stocks.framework.model.param.ModelParameters;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.File;
import java.util.List;

public abstract class ModelHandler<T extends PredictionModel>
{

    /**
     *
     * @return values are the default values for the hyper-parameters
     */
    public abstract List<HyperParameter> getRequiredHyperParameters();

    public abstract ModelParameters getConfiguration();

    public abstract T create();

    public abstract void train(T model, ProcessedDataset corpus);

    public abstract ModelEvaluation evaluate(T trainedModel, ProcessedDataset trainingData, ProcessedDataset testingData);

    public abstract double[] predictOne(T trainedModel, double[] features);

    public abstract INDArray predict(T trainedModel, INDArray features);

    public abstract boolean writeModel(File file, T trainedModel);

    public abstract T loadModel(File file);

}
