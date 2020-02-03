package net.ben.stocks.framework.thread;

import net.ben.stocks.framework.thread.internals.TaskCallback;

/**
 * @param <T> the Object the Task will return on finished
 */
public interface Task<T extends Result> extends Runnable
{

    Progress newTaskProgress();

    boolean isFinished();

    TaskCallback getFinishedCallback();

    T getResult();

}
