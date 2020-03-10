package net.benorourke.stocks.framework.thread.preprocessing;

public enum PreprocessingStage
{
    INITIALISE_PREPROCESSES,
    LOAD_QUOTES,
    LOAD_CORPUS,
    CLEAN_CORPUS,
    PROCESS_CORPUS,
    DONE;

    // Order of Document Processing:
    // Document[] -> CleanedDocument[] -> ProcessedCorpus (ModelData[])

    public static PreprocessingStage first()
    {
        return INITIALISE_PREPROCESSES;
    }

    PreprocessingStage next()
    {
        switch (this)
        {
            case INITIALISE_PREPROCESSES:
                return LOAD_QUOTES;

            case LOAD_QUOTES:
                return LOAD_CORPUS;

            case LOAD_CORPUS:
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
