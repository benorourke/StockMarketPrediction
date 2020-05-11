package net.benorourke.stocks.userinterface.scene.dashboard;

/**
 * The current stage of a Time Series.
 *
 * The flow of stages goes, in order:
 * 1) Collecting and Injecting Raw Data
 * 2) Pre-processing of Data using Raw Data
 * 3) Training & Evaluating Models using Pre-processed Data
 */
public enum FlowStage
{
    COLLECTING_AND_INJECTING(""),
    PRE_PROCESSED("You must pre-process data before you can do this!"),
    TRAINING_AND_EVALUATING_MODELS("You must train a model before evaluating them!");

    private final String errorMessage;

    FlowStage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public static FlowStage defaultStage()
    {
        return COLLECTING_AND_INJECTING;
    }

    /**
     * Check whether another stage is before this stage.
     *
     * @param currentStage
     * @return
     */
    public boolean isBefore(FlowStage currentStage)
    {
        return currentStage.ordinal() < ordinal();
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }
}
