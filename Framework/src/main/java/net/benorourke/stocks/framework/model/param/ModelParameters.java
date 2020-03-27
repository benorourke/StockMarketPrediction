package net.benorourke.stocks.framework.model.param;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ModelParameters
{
    private Map<String, Object> parameters;

    public ModelParameters(Map<String, Object> parameters)
    {
        this.parameters = parameters;
    }

    public ModelParameters()
    {
        this(new HashMap<>());
    }

    public void addMissingDefaults(Collection<HyperParameter> hyperParameters)
    {
        for (HyperParameter param : hyperParameters)
        {
            String key = param.getName().toUpperCase();

            if (!parameters.containsKey(key))
                parameters.put(key, param.getDefaultValue());
        }
    }

    public void add(String key, Object value)
    {
        parameters.put(key.toUpperCase(), value);
    }

    public Object get(String key)
    {
        return parameters.get(key.toUpperCase());
    }

    public int getInt(String key)
    {
        return (int) get(key);
    }

    public Map<String, Object> getParameters()
    {
        return parameters;
    }

}
