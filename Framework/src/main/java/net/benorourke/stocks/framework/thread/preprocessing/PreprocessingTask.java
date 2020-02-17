package net.benorourke.stocks.framework.thread.preprocessing;

import net.benorourke.stocks.framework.thread.Progress;
import net.benorourke.stocks.framework.thread.Task;
import net.benorourke.stocks.framework.thread.TaskDescription;
import net.benorourke.stocks.framework.thread.TaskType;

/**
 * No TaskDescription derivative class required as we only want to enable one
 * pre-processing task at a time.
 */
public class PreprocessingTask implements Task<TaskDescription, PreprocessingResult>
{
    private Progress progress;

    @Override
    public TaskType getType()
    {
        return TaskType.PRE_PROCESSING;
    }

    @Override
    public TaskDescription getDescription()
    {
        return new TaskDescription(TaskType.PRE_PROCESSING)
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
        // TODO
    }

    @Override
    public boolean isFinished()
    {
        return false;
    }

    @Override
    public PreprocessingResult getResult()
    {
        // TODO
        return new PreprocessingResult();
    }
}
