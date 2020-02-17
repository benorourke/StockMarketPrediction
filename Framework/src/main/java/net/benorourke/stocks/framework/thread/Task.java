package net.benorourke.stocks.framework.thread;

/**
 * @param <U>
 * @param <S> the Object the Task will return on finished
 */
public interface Task<U extends TaskDescription, S extends Result> extends Runnable
{

    TaskType getType();

    U getDescription();

    Progress createTaskProgress();

    boolean isFinished();

    S getResult();

}
