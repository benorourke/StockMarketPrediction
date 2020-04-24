package net.benorourke.stocks.framework.model.feedforward;

import net.benorourke.stocks.framework.model.ModelHandlerManager;
import net.benorourke.stocks.framework.model.ProcessedDataset;
import net.benorourke.stocks.framework.model.param.HyperParameter;
import net.benorourke.stocks.framework.model.param.ModelParameters;

import java.util.List;

public class FeedForwardRuntimeCreator implements ModelHandlerManager.RuntimeCreator<FeedForwardModelHandler>
{

    @Override
    public String name()
    {
        return "FEED_FORWARD";
    }

    @Override
    public FeedForwardModelHandler createFromParameters(ModelParameters parameters)
    {
        return new FeedForwardModelHandler(parameters);
    }

    @Override
    public FeedForwardModelHandler createFromDataset(ProcessedDataset dataset)
    {
        return new FeedForwardModelHandler(dataset.getNumFeatures(),
                                           FeedForwardModelHandler.HYPERPARAMETER_HIDDEN_NODES_DEFAULT,
                                           dataset.getNumLabels());
    }

    @Override
    public List<HyperParameter> getRequiredParameters()
    {
        return FeedForwardModelHandler.REQUIRED_HYPERPARAMETERS;
    }

}
