package net.benorourke.gatherer;

import net.benorourke.gatherer.exception.FailedRetrievalException;

import java.util.Collection;

public abstract class DataSource
{

    public abstract Collection<TextualData> retrieve() throws FailedRetrievalException;

}
