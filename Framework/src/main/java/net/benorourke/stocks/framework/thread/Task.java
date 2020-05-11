package net.benorourke.stocks.framework.thread;

import net.benorourke.stocks.framework.util.Nullable;

/**
 * A repeating task that can be scheduled through the {@link net.benorourke.stocks.framework.thread.TaskManager}.
 * @param <U> the type of TaskDescription for this task
 * @param <S> the type of Result that will be passed to the callback upon completion
 */
public interface Task<U extends TaskDescription, S extends Result> extends Runnable
{

    /**
     * Get the type of the task.
     *
     * @return the type
     */
    TaskType getType();

    /**
     * Get a description of this task so that collisions can be tested for when scheduling.
     *
     * @return the description
     */
    U getDescription();

    /**
     * Create an instance of the task Progress.
     *
     * This should be stored internally by the Task derivative once this is called.
     *
     * @return the progress
     */
    Progress createTaskProgress();

    /**
     * Has the repeating task finished yet?
     *
     * @return whether it is finished
     */
    boolean isFinished();

    /**
     * Once {@link #isFinished()} returns true, the TaskManager will call this.
     *
     * @return the result of the task
     */
    @Nullable
    S getResult();

}
