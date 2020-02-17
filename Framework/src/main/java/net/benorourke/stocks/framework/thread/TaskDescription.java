package net.benorourke.stocks.framework.thread;

/**
 * A description of a TaskWrapper so that tasks can be compared for equivalency in order to prevent
 * the same task running twice.
 */
public abstract class TaskDescription
{
    private final TaskType type;

    public TaskDescription(TaskType type)
    {
        this.type = type;
    }

    public abstract boolean equals(Object object);

    public TaskType getType()
    {
        return type;
    }
}
