package net.benorourke.stocks.framework.collection.datasource.twitter;

import net.benorourke.stocks.framework.collection.datasource.variable.CollectionVariable;

public abstract class PremiumTwitterEndpoint extends TwitterEndpoint
{
    @CollectionVariable(name = "Environment",
                        type = CollectionVariable.Type.STRING,
                        prompt = "Label of the premium environment")
    private String environment;

    public PremiumTwitterEndpoint(EndpointType type)
    {
        super(type);
    }

    public String getEnvironment()
    {
        return environment;
    }

}
