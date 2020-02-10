package net.ben.stocks.framework.thread;

/**
 * The Callback that returns the result of a Task.
 *
 * This is external and specific to the use, i.e., updating a user interface with the result.
 */
public interface ResultCallback<T extends Result>
{

    void onCallback(T result);

}
