package net.benorourke.stocks.framework.model.feedforward;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.model.ModelData;
import net.benorourke.stocks.framework.model.ModelEvaluation;
import net.benorourke.stocks.framework.model.ProcessedDataset;
import net.benorourke.stocks.framework.model.param.HyperParameter;
import net.benorourke.stocks.framework.model.param.ModelParameters;
import net.benorourke.stocks.framework.model.ModelHandler;
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
import java.util.*;

public class FeedForwardModelHandler extends ModelHandler<FeedForwardModel>
{
    private static final long SEED = 0;
    private static final List<HyperParameter> REQUIRED_HYPERPARAMETERS;

    public static String HYPERPARAMETER_HIDDEN_NODES = "Hidden Nodes";

    static
    {
        List<HyperParameter> list = new ArrayList<>();
        list.add(new HyperParameter(HYPERPARAMETER_HIDDEN_NODES, 30));
        REQUIRED_HYPERPARAMETERS = Collections.unmodifiableList(list);
    }

    private final int numFeatures, numLabels;

    public FeedForwardModelHandler(int numFeatures, int numLabels)
    {
        this.numFeatures = numFeatures;
        this.numLabels = numLabels;
    }

    @Override
    public FeedForwardModel create(ModelParameters configuration)
    {
        configuration.addMissingDefaults(getRequiredHyperParameters());
        int paramHidden = configuration.getInt(HYPERPARAMETER_HIDDEN_NODES);

        Framework.debug("Creating with features " + numFeatures + ", labels " + numLabels);
        Framework.debug("d1");
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(SEED)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(new Adam())
                .l2(1e-4)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(numFeatures).nOut(paramHidden)
                        .activation(Activation.TANH)
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nIn(paramHidden).nOut(numLabels).build())
                .build();
        Framework.debug("d2");

        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        net.setListeners(new ScoreIterationListener(1));
        Framework.debug("d3");

        return new FeedForwardModel(net);
    }

    @Override
    public List<HyperParameter> getRequiredHyperParameters()
    {
        return REQUIRED_HYPERPARAMETERS;
    }

    @Override
    public void train(FeedForwardModel model, ProcessedDataset corpus)
    {
        DataSetIterator iterator = new ExistingDataSetIterator(Arrays.asList(corpus.toDataSet(SEED)));

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
    public ModelEvaluation evaluate(FeedForwardModel trainedModel,
                                    ProcessedDataset trainingData, ProcessedDataset testingData)
    {
        List<ModelEvaluation.Prediction> trainingPredictions = getPredictions(trainedModel, trainingData);
        List<ModelEvaluation.Prediction> testingPredictions = getPredictions(trainedModel, testingData);
        return new ModelEvaluation(getScore(trainedModel, testingData), trainingPredictions, testingPredictions);
    }

    private double getScore(FeedForwardModel trainedModel, ProcessedDataset testingData)
    {
        RegressionEvaluation regressionEvaluation =  new RegressionEvaluation(numLabels);
        DataSet dataset = testingData.toDataSet(SEED);
        INDArray labels = dataset.getLabels();
        INDArray features = dataset.getFeatures();
        INDArray predicted = predict(trainedModel, features);
        regressionEvaluation.eval(labels, predicted);
        Framework.debug(regressionEvaluation.stats());
        return regressionEvaluation.rootMeanSquaredError(0); // TODO - Should we take an average of all the cols, in case there is?
    }

    private List<ModelEvaluation.Prediction> getPredictions(FeedForwardModel model, ProcessedDataset data)
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
    public double[] predictOne(FeedForwardModel trainedModel, double[] features)
    {
        // TODO - Test
        double[][] inputMatrix = new double[][]{ features };
        return trainedModel.predict(Nd4j.create(inputMatrix)).getRow(0).toDoubleVector();
    }

    @Override
    public INDArray predict(FeedForwardModel trainedModel, INDArray features)
    {
        return trainedModel.predict(features);
    }

    @Override
    public boolean writeModel(File file, FeedForwardModel trainedModel)
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
    public FeedForwardModel loadModel(File file)
    {
        try
        {
            return new FeedForwardModel(MultiLayerNetwork.load(file, true));
        }
        catch (IOException e)
        {
            Framework.error("Unable to load model from " + file.getPath(), e);
            return null;
        }
    }

}
