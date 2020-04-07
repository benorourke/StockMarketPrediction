package net.benorourke.stocks.framework.thread.preprocessing;

import net.benorourke.stocks.framework.model.ProcessedDataset;
import net.benorourke.stocks.framework.thread.Result;

public class PreprocessingResult extends Result
{
    private final ProcessedDataset corpus;

    public PreprocessingResult(ProcessedDataset corpus)
    {
        this.corpus = corpus;
    }

    public ProcessedDataset getCorpus()
    {
        return corpus;
    }

}
