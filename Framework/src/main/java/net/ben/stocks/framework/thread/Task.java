package net.ben.stocks.framework.thread;

/**
 * @param <T> the Object the Task will return on finished
 */
public interface Task<T extends Result> extends Runnable
{

    Progress newTaskProgress();

    boolean isFinished();

    T getResult();

}
