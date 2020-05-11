package net.benorourke.stocks.framework.model.param;

import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * A storage class for model parameters.
 */
public class ModelParameters
{

    /** Parameter identifiers (in uppercase) mapped against their value. */
    private LinkedHashMap<String, Integer> parameters;

    public ModelParameters(LinkedHashMap<String, Integer> parameters)
    {
        this.parameters = parameters;
    }

    public ModelParameters()
    {
        this(new LinkedHashMap<>());
    }

    /**
     * Add any missing parameters default value by the required hyper-parameters.
     *
     * @param hyperParameters the parameters
     */
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

    public LinkedHashMap<String, Integer> getParameters()
    {
        return parameters;
    }

}
