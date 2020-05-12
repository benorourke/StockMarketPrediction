package net.benorourke.stocks.examples.model;

import net.benorourke.stocks.framework.model.PredictionModel;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;

import java.io.File;
import java.io.IOException;

/**
 * A simple wrapper class for the DeepLearning4J MultiLayerNetwork class.
 */
public class FeedForwardTwoLayers extends PredictionModel
{
    private final MultiLayerNetwork network;

    public FeedForwardTwoLayers(MultiLayerNetwork network)
    {
        this.network = network;
    }

    public void fit(DataSet set)
    {
        network.fit(set);
    }

    public INDArray predict(INDArray input)
    {
        return network.output(input);
    }

    /**
     * Write the model to file
     *
     * @param file the file
     * @throws IOException exception thrown while doing so
     */
    public void save(File file) throws IOException
    {
        if (file.exists())
            file.delete();
        file.getParentFile().mkdirs();

        network.save(file);
    }

}
