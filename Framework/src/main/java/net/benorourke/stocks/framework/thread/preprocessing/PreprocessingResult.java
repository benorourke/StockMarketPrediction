package net.benorourke.stocks.framework.thread.preprocessing;

import net.benorourke.stocks.framework.model.ProcessedDataset;
import net.benorourke.stocks.framework.thread.Result;

/**
 * The pre-processing result. Simply a processed dataset.
 */
public class PreprocessingResult extends Result
{
    private final ProcessedDataset dataset;

    public PreprocessingResult(ProcessedDataset dataset)
    {
        this.dataset = dataset;
    }

    public ProcessedDataset getDataset()
    {
        return dataset;
    }

}
