package net.benorourke.stocks.framework.exception;


import java.io.IOException;

public class FailedCollectionException extends Exception
{
    public enum Type {HTTP_ERROR, RESPONSE_CODE, COLLECTION_VARIABLE_INVALID, TWITTER_EXCEPTION}

    private final Type type;

    public FailedCollectionException(Type type, String message)
    {
        super (message);

        this.type = type;
    }

    public FailedCollectionException(IOException exception)
    {
        this (Type.HTTP_ERROR, exception.getMessage());
    }

    public FailedCollectionException(int responseCode)
    {
        this (Type.RESPONSE_CODE, "Invalid response code: " + responseCode);
    }

    public Type getType()
    {
        return type;
    }

}
