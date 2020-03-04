package net.benorourke.stocks.framework.thread.model;

import net.benorourke.stocks.framework.thread.*;

/**
 * No TaskDescription derivative class required as we only want to enable one
 * pre-processing task at a time.
 */
public class TrainingTask implements Task<TaskDescription, Result>
{
    private Progress progress;

    @Override
    public TaskType getType()
    {
        return TaskType.TRAINING;
    }

    @Override
    public TaskDescription getDescription()
    {
        return new TaskDescription(TaskType.TRAINING)
        {
            @Override
            public boolean equals(Object object)
            {
                return object instanceof TaskDescription
                        && ((TaskDescription) object).getType().equals(getType());
            }
        };
    }

    @Override
    public Progress createTaskProgress()
    {
        return progress = new Progress();
    }

    @Override
    public void run()
    {

    }

    @Override
    public boolean isFinished()
    {
        return false;
    }

    @Override
    public Result getResult()
    {
        return new Result();
    }
}
