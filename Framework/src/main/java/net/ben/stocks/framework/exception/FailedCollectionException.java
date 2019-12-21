package net.ben.stocks.framework.exception;

public class FailedCollectionException extends Exception
{

    /**
     * TODO - Add more data about the failed collection, i.e., the source, why, etc.
     */

    public FailedCollectionException(Exception exception)
    {
        super(exception);
    }

}
