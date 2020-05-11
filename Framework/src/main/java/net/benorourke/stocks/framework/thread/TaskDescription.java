package net.benorourke.stocks.framework.thread;

/**
 * A description of a TaskWrapper so that tasks can be compared for equivalency in order to prevent
 * scheduling two tasks that may affect each other.
 */
public abstract class TaskDescription
{
    private final TaskType type;

    public TaskDescription(TaskType type)
    {
        this.type = type;
    }

    /**
     * The method used to compare the equivalency of two task types.
     *
     * @param object the other TaskDescription
     * @return whether if these two tasks were to be concurrently scheduled, issues could be faced
     */
    public abstract boolean equals(Object object);

    public TaskType getType()
    {
        return type;
    }
}
