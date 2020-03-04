package net.benorourke.stocks.framework.model;

import net.benorourke.stocks.framework.Framework;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.SequenceRecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.records.reader.impl.csv.CSVSequenceRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.NumberedFileInputSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.datasets.datavec.SequenceRecordReaderDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;

public class ModelFactory
{
    private static final int nInput = 5, nHidden = 10, nOutput = 5;
    private static final int batchSize = 5;

    private ModelFactory() {}

    public static MultiLayerNetwork build(long seed)
    {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                                            .seed(seed)
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

        return net;
    }

    public static MultiLayerNetwork build()
    {
        return build(0);
    }

    public static void train(MultiLayerNetwork net, File recordsFile)
    {
        RecordReader recordReader = new CSVRecordReader(1, ",");
        try
        {
            recordReader.initialize(new FileSplit(recordsFile));
        }
        catch (Exception exception)
        {
            Framework.error("Unable to build network", exception);
            return ;
        }

        boolean regression = true;
        int labelFrom = nInput;
        int labelTo = labelFrom + nOutput - 1;
        DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, batchSize, labelFrom, labelTo, regression);

        //Number of epochs (full passes of the data)
        final int nEpochs = 200;
        //Train the network on the full data set, and evaluate in periodically
        for (int i=0; i < nEpochs; i++ )
        {
            while (iterator.hasNext()) net.fit(iterator.next()); // fit model using mini-batch data
            iterator.reset(); // reset iterator
        }
    }

}
