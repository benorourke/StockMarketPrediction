package net.benorourke.stocks.examples.model;

import net.benorourke.stocks.framework.Framework;

public class ModelExample
{

    public static void main(String[] args)
    {
        Framework framework = new Framework();
        framework.initialise();
        framework.getModelHandlerManager().getCreators().add(new FeedForwardTwoLayersRuntimeCreator());
    }

}
