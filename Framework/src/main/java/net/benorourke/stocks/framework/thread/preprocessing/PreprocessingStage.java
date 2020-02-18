package net.benorourke.stocks.framework.thread.preprocessing;

public enum PreprocessingStage
{
    INITIALISE_PREPROCESSORS,
    LOADING_QUOTES,
    PROCESSING_QUOTES,
    DONE;

    // Order of Document Processing:
    //

    public static PreprocessingStage first()
    {
        return INITIALISE_PREPROCESSORS;
    }

    PreprocessingStage next()
    {
        switch (this)
        {
            case INITIALISE_PREPROCESSORS:
                return LOADING_QUOTES;
            case LOADING_QUOTES:
                return PROCESSING_QUOTES;
            case PROCESSING_QUOTES:
                return DONE;

            case DONE:
                return DONE;
            default:
                return null;
        }
    }

}
