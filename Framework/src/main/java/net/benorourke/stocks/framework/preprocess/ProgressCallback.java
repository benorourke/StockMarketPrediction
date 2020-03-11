package net.benorourke.stocks.framework.preprocess;

/**
 * A callback for every time the progress increments by a significant amount.
 *
 * Can be added to Preprocessors; where the Preprocess should regularly call
 * onProgressChange.
 *
 * {@link #onProgressUpdate(double)} should be called with 100% when the task is completed.
 */
public interface ProgressCallback
{

    void onProgressUpdate(double percentageProgress);

}
