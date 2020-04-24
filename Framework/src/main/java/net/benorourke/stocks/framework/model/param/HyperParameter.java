package net.benorourke.stocks.framework.model.param;

public class HyperParameter
{
    private final String name;
    private final boolean selfGenerated;
    private final int defaultValue;

    /**
     *
     * @param name
     * @param selfGenerated whether the user should have to define this
     * @param defaultValue
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
