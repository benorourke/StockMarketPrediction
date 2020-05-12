package net.benorourke.stocks.examples.model;

import net.benorourke.stocks.framework.model.ModelHandlerManager;
import net.benorourke.stocks.framework.model.ProcessedDataset;
import net.benorourke.stocks.framework.model.param.HyperParameter;
import net.benorourke.stocks.framework.model.param.ModelParameters;

import java.util.List;

public class FeedForwardTwoLayersRuntimeCreator implements ModelHandlerManager.RuntimeCreator<FeedForwardTwoLayersModelHandler>
{

    @Override
    public String name()
    {
        return "FEED_FORWARD";
    }

    @Override
    public FeedForwardTwoLayersModelHandler createFromParameters(long seed, ModelParameters parameters)
    {
        return new FeedForwardTwoLayersModelHandler(seed, parameters);
    }

    @Override
    public FeedForwardTwoLayersModelHandler createFromDataset(long seed, ProcessedDataset dataset)
    {
        return new FeedForwardTwoLayersModelHandler(seed, dataset.getNumFeatures(),
                                           FeedForwardTwoLayersModelHandler.HYPERPARAMETER_HIDDEN_NODES_1_DEFAULT,
                                           FeedForwardTwoLayersModelHandler.HYPERPARAMETER_HIDDEN_NODES_2_DEFAULT,
                                           dataset.getNumLabels());
    }

    @Override
    public List<HyperParameter> getRequiredParameters()
    {
        return FeedForwardTwoLayersModelHandler.REQUIRED_HYPERPARAMETERS;
    }

}
