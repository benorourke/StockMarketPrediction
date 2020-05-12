package net.benorourke.stocks.framework.model;

import net.benorourke.stocks.framework.model.param.HyperParameter;
import net.benorourke.stocks.framework.model.param.ModelParameters;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.File;
import java.util.List;

/**
 * Provides an abstraction away from handling prediction models individually, since different libraries may be used
 * to create different models.
 *
 * @param <T> the PredictionModel that is being abstracted away.
 */
public abstract class ModelHandler<T extends PredictionModel>
{

    /**
     *
     * @return values are the default values for the hyper-parameters
     */
    public abstract List<HyperParameter> getRequiredHyperParameters();

    /**
     * Get the configuration that will be used when training using this model handler.
     *
     * @return the set of parameters
     */
    public abstract ModelParameters getConfiguration();

    /**
     * Create the prediction model instance.
     *
     * @return the instance
     */
    public abstract T create();

    /**
     * Train a prediction model on data.
     *
     * @param model the model to train
     * @param corpus the training dataset
     */
    public abstract void train(T model, ProcessedDataset corpus);

    /**
     * Evaluate a trained model by creating an evaluation object based on training data and testing data.
     *
     * @param trainedModel the model to evaluate
     * @param trainingData the training data
     * @param testingData the testing data
     * @return the evaluation object
     */
    public abstract ModelEvaluation evaluate(T trainedModel, ProcessedDataset trainingData, ProcessedDataset testingData);

    /**
     * Predict labels from features for a given trained model.
     *
     * @param trainedModel the model to predict from
     * @param features the inputs to predict outputs for
     * @return the predictions
     */
    public abstract double[] predictOne(T trainedModel, double[] features);

    /**
     * Predict a matrix of features for a given trained model.
     *
     * @param trainedModel the model to predict from
     * @param features the matrix of features to predict from
     * @return the predictions in a matrix
     */
    public abstract INDArray predict(T trainedModel, INDArray features);

    /**
     * Write a model to file.
     *
     * @param file the file
     * @param trainedModel the model to write
     * @return whether it was successfully written
     */
    public abstract boolean writeModel(File file, T trainedModel);

    /**
     * Load a model from file.
     *
     * @param file the file
     * @return the model
     */
    public abstract T loadModel(File file);

}
