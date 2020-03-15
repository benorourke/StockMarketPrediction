package net.benorourke.stocks.framework.model.feedforward;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.model.ModelHandler;
import net.benorourke.stocks.framework.model.ProcessedCorpus;
import org.deeplearning4j.datasets.iterator.ExistingDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import static net.benorourke.stocks.framework.model.ModelData.*;

import java.util.Arrays;

public class FeedForwardModelHandler extends ModelHandler<FeedForwardModel>
{
    private static final int N_HIDDEN = 30;

    private final int numFeatures, numLabels;

    public FeedForwardModelHandler(long seed, int numFeatures, int numLabels)
    {
        super(seed);

        this.numFeatures = numFeatures;
        this.numLabels = numLabels;
    }

    @Override
    public FeedForwardModel create()
    {
        Framework.debug("Creating with features " + numFeatures + ", labels " + numLabels);
        Framework.debug("d1");
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(getSeed())
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(new Adam())
                .l2(1e-4)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(numFeatures).nOut(N_HIDDEN)
                        .activation(Activation.TANH)
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nIn(N_HIDDEN).nOut(numLabels).build())
                .build();
        Framework.debug("d2");

        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        net.setListeners(new ScoreIterationListener(1));
        Framework.debug("d3");

        return new FeedForwardModel(net);
    }

    @Override
    public void train(FeedForwardModel model, ProcessedCorpus corpus)
    {
        DataSetIterator iterator = new ExistingDataSetIterator(
                Arrays.asList(corpus.toDataSet(getSeed())));

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
    public void evaluate(FeedForwardModel trainedModel, DataSet dataSet)
    {
        // TODO
        Evaluation evaluation = new Evaluation(numLabels);

        INDArray labels = dataSet.getLabels();
        INDArray features = dataSet.getFeatures();
        INDArray predicted = predict(trainedModel, features);
        evaluation.eval(labels, predicted);
        Framework.debug(evaluation.stats());
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

}
