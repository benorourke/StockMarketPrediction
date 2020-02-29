package net.benorourke.stocks.framework.thread.preprocessing;

public enum PreprocessingStage
{
    INITIALISE_PREPROCESSES,
    LOADING_QUOTES,
    PROCESSING_QUOTES,
    LOADING_CORPUS,
    CLEAN_CORPUS,
    PROCESS_CORPUS,
    DONE;

    // Order of Document Processing:
    // Document[] -> CleanedDocument[] -> ProcessedCorpus (ProcessedDocument[])

    public static PreprocessingStage first()
    {
        return INITIALISE_PREPROCESSES;
    }

    PreprocessingStage next()
    {
        switch (this)
        {
            case INITIALISE_PREPROCESSES:
                return LOADING_QUOTES;

            case LOADING_QUOTES:
                return PROCESSING_QUOTES;
            case PROCESSING_QUOTES:
                return LOADING_CORPUS;

            case LOADING_CORPUS:
                return CLEAN_CORPUS;
            case CLEAN_CORPUS:
                return PROCESS_CORPUS;
            case PROCESS_CORPUS:
                return DONE;

            case DONE:
                return DONE;
            default:
                return null;
        }
    }

}
