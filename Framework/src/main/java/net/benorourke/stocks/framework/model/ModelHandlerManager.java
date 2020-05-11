package net.benorourke.stocks.framework.model;

import net.benorourke.stocks.framework.model.feedforward.FeedForwardModelHandler;
import net.benorourke.stocks.framework.model.feedforward.FeedForwardRuntimeCreator;
import net.benorourke.stocks.framework.model.param.HyperParameter;
import net.benorourke.stocks.framework.model.param.ModelParameters;
import net.benorourke.stocks.framework.util.Initialisable;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores all the model handlers; allowing for the injection of additional model handlers.
 *
 */
public class ModelHandlerManager implements Initialisable
{
    public static final RuntimeCreator<FeedForwardModelHandler> FEED_FORWARD_CREATOR = new FeedForwardRuntimeCreator();

    /**
     * The cache of model handlers that can be created at runtime.
     */
    private final List<RuntimeCreator> creators;

    public ModelHandlerManager()
    {
        creators = new ArrayList<>();
    }

    @Override
    public void initialise()
    {
        // By default, add a Feed Forward Creator
        creators.add(FEED_FORWARD_CREATOR);
    }

    public List<RuntimeCreator> getCreators()
    {
        return creators;
    }

    /**
     *  Needs dynamic creation since differing corpuses can have differing number of inputs / outputs so these
     *  need to be specified at runtime.
     *
     * @param <T> the type of ModelHandler to be created at runtime
     */
    public interface RuntimeCreator<T extends ModelHandler>
    {

        String name();

        List<HyperParameter> getRequiredParameters();

        T createFromParameters(long seed, ModelParameters parameters);

        T createFromDataset(long seed, ProcessedDataset dataset);

    }

}
