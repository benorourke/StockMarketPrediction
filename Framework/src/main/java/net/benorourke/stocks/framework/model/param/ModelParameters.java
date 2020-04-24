package net.benorourke.stocks.framework.model.param;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ModelParameters
{
    private Map<String, Integer> parameters;

    public ModelParameters(Map<String, Integer> parameters)
    {
        this.parameters = parameters;
    }

    public ModelParameters()
    {
        this(new HashMap<>());
    }

    public void setMissingDefaults(Collection<HyperParameter> hyperParameters)
    {
        for (HyperParameter param : hyperParameters)
        {
            String key = param.getName().toUpperCase();

            if (!parameters.containsKey(key))
                parameters.put(key, param.getDefaultValue());
        }
    }

    public void set(String key, Integer value)
    {
        parameters.put(key.toUpperCase(), value);
    }

    public int get(String key)
    {
        return parameters.get(key.toUpperCase());
    }

    public Map<String, Integer> getParameters()
    {
        return parameters;
    }

}
