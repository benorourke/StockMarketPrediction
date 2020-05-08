package net.benorourke.stocks.framework.series.data;

import java.util.UUID;

/**
 * For raw data so that it can be efficiently distinguished when querying a single piece of data.
 */
public interface IdentifiableData
{

    UUID getId();

}
