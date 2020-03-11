package net.benorourke.stocks.framework.thread;

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
