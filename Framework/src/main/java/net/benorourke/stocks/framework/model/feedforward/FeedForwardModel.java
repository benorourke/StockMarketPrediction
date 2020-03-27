package net.benorourke.stocks.framework.model.feedforward;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.model.PredictionModel;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;

import java.io.File;
import java.io.IOException;

public class FeedForwardModel extends PredictionModel
{
    private final MultiLayerNetwork network;

    public FeedForwardModel(MultiLayerNetwork network)
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

    public void save(File file) throws IOException
    {
        if (file.exists())
            file.delete();
        file.getParentFile().mkdirs();

        network.save(file);
    }

}
