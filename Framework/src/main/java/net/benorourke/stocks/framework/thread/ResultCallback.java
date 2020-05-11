package net.benorourke.stocks.framework.thread;

/**
 * The Callback that returns the result of a Task.
 *
 * This is external and specific to the use, i.e., updating a user interface with the result.
 *
 * @param <T> the type of the result
 */
public interface ResultCallback<T extends Result>
{

    /**
     * The method called once a task is complete.
     *
     * @param result the result object
     */
    void onCallback(T result);

}
