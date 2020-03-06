package net.benorourke.stocks.framework.model.feedforward;

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
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.Arrays;

public class FeedForwardModelHandler extends ModelHandler<FeedForwardModel>
{
    private static final int nInput = 5, nHidden = 10, nOutput = 5;
    private static final int batchSize = 5;

    public FeedForwardModelHandler(long seed)
    {
        super(seed);
    }

    public FeedForwardModelHandler()
    {
        this(0);
    }

    @Override
    public FeedForwardModel create()
    {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(getSeed())
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(new Adam())
                .l2(1e-4)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(nInput).nOut(nHidden)
                        .activation(Activation.TANH)
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nIn(nHidden).nOut(nOutput).build())
                .pretrain(false).backprop(true)
                .build();

        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        net.setListeners(new ScoreIterationListener(1));

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
    public void evaluate(FeedForwardModel trainedModel)
    {

    }

}
