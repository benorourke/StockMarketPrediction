package net.ben.stocks.framework.exception;

import java.io.IOException;

public class FailedCollectionException extends Exception
{
    public enum Type {HTTP_ERROR, RESPONSE_CODE};

    private final Type type;
    private final int responseCode;

    public FailedCollectionException(IOException httpException)
    {
        super(httpException);

        type = Type.HTTP_ERROR;
        responseCode = -1;
    }

    public FailedCollectionException(int responseCode)
    {
        super("Invalid response code " + responseCode);

        type = Type.RESPONSE_CODE;
        this.responseCode = responseCode;
    }

    public Type getType()
    {
        return type;
    }

    public int getResponseCode()
    {
        return responseCode;
    }

}
