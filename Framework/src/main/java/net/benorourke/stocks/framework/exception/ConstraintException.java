package net.benorourke.stocks.framework.exception;

/**
 * Exception thrown when a constraint failed.
 */
public class ConstraintException extends Exception
{

    public ConstraintException(String message)
    {
        super(message);
    }

}
