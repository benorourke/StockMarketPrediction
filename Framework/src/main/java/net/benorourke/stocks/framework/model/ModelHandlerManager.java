package net.benorourke.stocks.framework.model;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.model.feedforward.FeedForwardModelHandler;
import net.benorourke.stocks.framework.model.param.HyperParameter;
import net.benorourke.stocks.framework.util.Initialisable;

import java.util.ArrayList;
import java.util.List;

public class ModelHandlerManager implements Initialisable
{
    public static final RuntimeCreator<FeedForwardModelHandler> FEED_FORWARD_CREATOR = new RuntimeCreator<FeedForwardModelHandler>()
    {

        @Override
        public String name()
        {
            return "FEED_FORWARD";
        }

        @Override
        public FeedForwardModelHandler createFromCorpus(ProcessedCorpus corpus)
        {
            return new FeedForwardModelHandler(corpus.getNumFeatures(), corpus.getNumLabels());
        }

        @Override
        public List<HyperParameter> getRequiredParameters()
        {
            return new FeedForwardModelHandler(0, 0).getRequiredHyperParameters();
        }

    };

    private final List<RuntimeCreator> creators;

    public ModelHandlerManager()
    {
        creators = new ArrayList<>();
    }

    @Override
    public void initialise()
    {
        creators.add(FEED_FORWARD_CREATOR);
    }

    public ModelHandler createByName(String name, ProcessedCorpus corpus)
    {
        return creators.stream()
                        .filter(c -> c.name().equalsIgnoreCase(name))
                        .findFirst()
                        .orElse(null)
                    .createFromCorpus(corpus);
    }

    public List<RuntimeCreator> getCreators()
    {
        return creators;
    }

    /**
     *  Needs dynamic creation since differing corpuses can have differing number of inputs / outputs so these
     *  need to be specified at runtime.
     *
     * @param <T>
     */
    public interface RuntimeCreator<T extends ModelHandler>
    {

        String name();

        T createFromCorpus(ProcessedCorpus corpus);

        List<HyperParameter> getRequiredParameters();

    }

}
