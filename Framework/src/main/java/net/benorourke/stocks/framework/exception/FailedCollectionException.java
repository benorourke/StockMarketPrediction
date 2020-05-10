package net.benorourke.stocks.framework.exception;


import net.benorourke.stocks.framework.collection.datasource.DataSource;

import java.io.IOException;

public class FailedCollectionException extends Exception
{
    public enum Type {HTTP_ERROR, RESPONSE_CODE, COLLECTION_VARIABLE_INVALID, TWITTER_EXCEPTION}

    private final DataSource source;
    private final Type type;

    public FailedCollectionException(DataSource source, Type type, String message)
    {
        super (message);

        this.source = source;
        this.type = type;
    }

    public FailedCollectionException(DataSource source, IOException exception)
    {
        this (source, Type.HTTP_ERROR, exception.getMessage());
    }

    public FailedCollectionException(DataSource source, int responseCode)
    {
        this (source, Type.RESPONSE_CODE, "Invalid response code: " + responseCode);
    }

    public Type getType()
    {
        return type;
    }

    public DataSource getSource()
    {
        return source;
    }

}
