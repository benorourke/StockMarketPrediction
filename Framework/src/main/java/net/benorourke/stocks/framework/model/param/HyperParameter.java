package net.benorourke.stocks.framework.model.param;

public class HyperParameter
{
    private final String name;
    private final int defaultValue;

    public HyperParameter(String name, int defaultValue)
    {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getName()
    {
        return name;
    }

    public int getDefaultValue()
    {
        return defaultValue;
    }

}
