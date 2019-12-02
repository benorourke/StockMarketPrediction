package net.benorourke.gatherer.exception;

public class FailedRetrievalException extends Exception
{

    public FailedRetrievalException(Exception exception)
    {
        super(exception);
    }

}
