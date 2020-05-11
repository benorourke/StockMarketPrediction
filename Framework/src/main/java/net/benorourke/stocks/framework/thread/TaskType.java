package net.benorourke.stocks.framework.thread;


/**
 * Interface rather than enum so custom Tasks can be created at runtime.
 */
public interface TaskType
{
    TaskType COLLECTION = new TaskType()
    {
        @Override
        public String getName()
        {
            return "Collection";
        }
    };
    TaskType PRE_PROCESSING = new TaskType()
    {
        @Override
        public String getName()
        {
            return "Pre-processing";
        }
    };
    TaskType TRAINING = new TaskType()
    {
        @Override
        public String getName()
        {
            return "Training";
        }
    };

    String getName();
}
