package net.benorourke.stocks.framework.model.param;

public class HyperParameter
{
    private final String name;
    private final Object defaultValue;

    public HyperParameter(String name, Object defaultValue)
    {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getName()
    {
        return name;
    }

    public Object getDefaultValue()
    {
        return defaultValue;
    }

}
