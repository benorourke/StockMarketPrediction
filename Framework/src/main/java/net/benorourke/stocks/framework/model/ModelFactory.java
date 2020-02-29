package net.benorourke.stocks.framework.model;

import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Nadam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class ModelFactory
{

    private ModelFactory() {}

    public static MultiLayerNetwork build(long seed)
    {
//        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
//                .seed(seed)    //Random number generator seed for improved repeatability. Optional.
//                .weightInit(WeightInit.XAVIER)
//                .updater(new Nadam())
//                .gradientNormalizationThreshold(0.5)
//                .list()
//                .layer(new LSTM.Builder().activation(Activation.TANH).nIn(1).nOut(10).build())
//                .layer(new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
//                        .activation(Activation.SOFTMAX)
//                        .nIn(10)
//                        .nOut(numLabelClasses).build())
//                .build();
        return new MultiLayerNetwork(null);
    }

    public static MultiLayerNetwork build()
    {
        return build(0);
    }

}
