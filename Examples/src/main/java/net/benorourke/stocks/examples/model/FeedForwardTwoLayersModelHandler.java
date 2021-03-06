package net.benorourke.stocks.examples.model;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.model.ModelData;
import net.benorourke.stocks.framework.model.ModelEvaluation;
import net.benorourke.stocks.framework.model.ModelHandler;
import net.benorourke.stocks.framework.model.ProcessedDataset;
import net.benorourke.stocks.framework.model.param.HyperParameter;
import net.benorourke.stocks.framework.model.param.ModelParameters;
import org.deeplearning4j.datasets.iterator.ExistingDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.regression.RegressionEvaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Handler for Feed Forward Models with 1 Hidden Layer.
 */
public class FeedForwardTwoLayersModelHandler extends ModelHandler<FeedForwardTwoLayers>
{
    /** A list of the hyper-parameters required to train a feedforward model. */
    public static final List<HyperParameter> REQUIRED_HYPERPARAMETERS;
    public static final String HYPERPARAMETER_INPUT_NODES = "Input Nodes";
    public static final String HYPERPARAMETER_HIDDEN_NODES_1 = "Hidden Nodes (Layer 1)";
    public static final String HYPERPARAMETER_HIDDEN_NODES_2 = "Hidden Nodes (Layer 2)";
    public static final String HYPERPARAMETER_OUTPUT_NODES = "Output Nodes";
    public static final int HYPERPARAMETER_HIDDEN_NODES_1_DEFAULT = 100;
    public static final int HYPERPARAMETER_HIDDEN_NODES_2_DEFAULT = 100;

    static
    {
        List<HyperParameter> list = new ArrayList<>();
        // Input & Output nodes are specified by the number of features & labels within the dataset
        list.add(new HyperParameter(HYPERPARAMETER_INPUT_NODES, false,0));
        list.add(new HyperParameter(HYPERPARAMETER_HIDDEN_NODES_1, true, HYPERPARAMETER_HIDDEN_NODES_1_DEFAULT));
        list.add(new HyperParameter(HYPERPARAMETER_HIDDEN_NODES_2, true, HYPERPARAMETER_HIDDEN_NODES_2_DEFAULT));
        list.add(new HyperParameter(HYPERPARAMETER_OUTPUT_NODES, false, 0));

        REQUIRED_HYPERPARAMETERS = Collections.unmodifiableList(list);
    }

    private final long seed;
    private final ModelParameters configuration;

    /**
     * Create a new instance based on Model Parameters; missing parameters will have default values et.
     *
     * @param seed the seed for this handler
     * @param configuration the parameters
     */
    protected FeedForwardTwoLayersModelHandler(long seed, ModelParameters configuration)
    {
        this.seed = seed;
        this.configuration = configuration;
        configuration.setMissingDefaults(getRequiredHyperParameters());
    }

    public FeedForwardTwoLayersModelHandler(long seed, int numInputs, int numHidden1, int numHidden2, int numOutputs)
    {
        this.seed = seed;
        configuration = new ModelParameters();
        configuration.set(HYPERPARAMETER_INPUT_NODES, numInputs);
        configuration.set(HYPERPARAMETER_HIDDEN_NODES_1, numHidden1);
        configuration.set(HYPERPARAMETER_HIDDEN_NODES_2, numHidden2);
        configuration.set(HYPERPARAMETER_OUTPUT_NODES, numOutputs);
        configuration.setMissingDefaults(getRequiredHyperParameters());
    }

    @Override
    public List<HyperParameter> getRequiredHyperParameters()
    {
        return REQUIRED_HYPERPARAMETERS;
    }

    @Override
    public ModelParameters getConfiguration()
    {
        return configuration;
    }

    @Override
    public FeedForwardTwoLayers create()
    {
        int paramFeatures = configuration.get(HYPERPARAMETER_INPUT_NODES);
        int paramHidden1 = configuration.get(HYPERPARAMETER_HIDDEN_NODES_1);
        int paramHidden2 = configuration.get(HYPERPARAMETER_HIDDEN_NODES_2);
        int paramLabels = configuration.get(HYPERPARAMETER_OUTPUT_NODES);

        Framework.info("Creating Feed Forward Model with features " + paramFeatures + ", labels " + paramLabels);
        // Create a FF-NN configuration with an stochastic gradient descent optimizer, Adam updater and 1 hidden layer
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(new Adam())
                .l2(1e-4)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(paramFeatures).nOut(paramHidden1)
                        .activation(Activation.TANH)
                        .build())
                .layer(1, new DenseLayer.Builder().nIn(paramHidden1).nOut(paramHidden2)
                        .activation(Activation.TANH)
                        .build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nIn(paramHidden2).nOut(paramLabels).build())
                .build();

        // Instantiate the network here
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        net.setListeners(new ScoreIterationListener(1));

        return new FeedForwardTwoLayers(net);
    }

    @Override
    public void train(FeedForwardTwoLayers model, ProcessedDataset corpus)
    {
        DataSetIterator iterator = new ExistingDataSetIterator(Arrays.asList(corpus.toDataSet(seed)));

        //Number of epochs (full passes of the feedforward)
        final int nEpochs = 200;
        //Train the network on the full feedforward set, and evaluate in periodically
        for (int i = 0; i < nEpochs; i ++)
        {
            while (iterator.hasNext())
                model.fit(iterator.next()); // fit model using mini-batch feedforward

            iterator.reset(); // reset iterator
        }
    }

    @Override
    public ModelEvaluation evaluate(FeedForwardTwoLayers trainedModel,
                                    ProcessedDataset trainingData, ProcessedDataset testingData)
    {
        List<ModelEvaluation.Prediction> trainingPredictions = getPredictions(trainedModel, trainingData);
        List<ModelEvaluation.Prediction> testingPredictions = getPredictions(trainedModel, testingData);
        return new ModelEvaluation(getScore(trainedModel, testingData), trainingPredictions, testingPredictions);
    }

    /**
     * Get the RMSE for a testing data set.
     *
     * @param trainedModel the model
     * @param testingData the testing data
     * @return
     */
    private double getScore(FeedForwardTwoLayers trainedModel, ProcessedDataset testingData)
    {
        RegressionEvaluation regressionEvaluation =  new RegressionEvaluation(configuration.get(HYPERPARAMETER_OUTPUT_NODES));
        DataSet dataset = testingData.toDataSet(seed);
        INDArray labels = dataset.getLabels();
        INDArray features = dataset.getFeatures();
        INDArray predicted = predict(trainedModel, features);
        regressionEvaluation.eval(labels, predicted);
        Framework.debug(regressionEvaluation.stats());
        return regressionEvaluation.rootMeanSquaredError(0);
    }

    /**
     * Get the predicted values for a given dataset.
     *
     * @param model the model
     * @param data the dataset
     * @return
     */
    private List<ModelEvaluation.Prediction> getPredictions(FeedForwardTwoLayers model, ProcessedDataset data)
    {
        List<ModelEvaluation.Prediction> days = new ArrayList<>();
        for (ModelData elem : data.getData())
        {
            double[] predicted = predictOne(model, elem.getFeatures());
            days.add(new ModelEvaluation.Prediction(elem.getDate(), elem.getLabels(), predicted));
        }
        return days;
    }

    @Override
    public double[] predictOne(FeedForwardTwoLayers trainedModel, double[] features)
    {
        double[][] inputMatrix = new double[][]{ features };
        return trainedModel.predict(Nd4j.create(inputMatrix)).getRow(0).toDoubleVector();
    }

    @Override
    public INDArray predict(FeedForwardTwoLayers trainedModel, INDArray features)
    {
        return trainedModel.predict(features);
    }

    @Override
    public boolean writeModel(File file, FeedForwardTwoLayers trainedModel)
    {
        try
        {
            trainedModel.save(file);
            return true;
        }
        catch (IOException e)
        {
            Framework.error("Unable to write model to " + file.getPath(), e);
            return false;
        }
    }

    @Override
    public FeedForwardTwoLayers loadModel(File file)
    {
        try
        {
            return new FeedForwardTwoLayers(MultiLayerNetwork.load(file, true));
        }
        catch (IOException e)
        {
            Framework.error("Unable to load model from " + file.getPath(), e);
            return null;
        }
    }

}
