package net.benorourke.stocks.framework.model.param;

/**
 * Specifies a hyper-parameter for training a model.
 */
public class HyperParameter
{
    private final String name;
    private final boolean selfGenerated;
    private final int defaultValue;

    /**
     * Create a inew instance.
     *
     * @param name the identifier of the hyper-parameter
     * @param selfGenerated whether the user should have to define this or it is inferred from, and is dependant on,
     *                      the pre-processed data (i.e. number of inputs / outputs)
     * @param defaultValue the hyper-parameters default value
     */
    public HyperParameter(String name, boolean selfGenerated, int defaultValue)
    {
        this.name = name;
        this.selfGenerated = selfGenerated;
        this.defaultValue = defaultValue;
    }

    public String getName()
    {
        return name;
    }

    public boolean isSelfGenerated()
    {
        return selfGenerated;
    }

    public int getDefaultValue()
    {
        return defaultValue;
    }

}
