package net.benorourke.stocks.framework.model.feedforward;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.model.PredictionModel;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.dataset.DataSet;

public class FeedForwardModel extends PredictionModel
{
    private final MultiLayerNetwork network;

    public FeedForwardModel(MultiLayerNetwork network)
    {
        this.network = network;
    }

    public void fit(DataSet set)
    {
        Framework.debug("DataSet null: " + (set == null));
        Framework.debug("Network null: " + (network == null));
        network.fit();
    }

}
