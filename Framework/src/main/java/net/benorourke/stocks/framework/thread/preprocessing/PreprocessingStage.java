package net.benorourke.stocks.framework.thread.preprocessing;

public enum PreprocessingStage
{
    INITIALISE_PREPROCESSES,

    LOAD_QUOTES,
    LOAD_CORPUS,

    DIMENSIONALITY_REDUCTION,
    FEATURE_REPRESENTATION,
    LABEL_ASSIGNMENT,

    DONE;

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
                return DIMENSIONALITY_REDUCTION;

            case DIMENSIONALITY_REDUCTION:
                return FEATURE_REPRESENTATION;
            case FEATURE_REPRESENTATION:
                return LABEL_ASSIGNMENT;
            case LABEL_ASSIGNMENT:
                return DONE;

            case DONE:
                return DONE;
            default:
                return null;
        }
    }

}
