package net.benorourke.stocks.framework.thread.preprocessing;

import net.benorourke.stocks.framework.preprocess.impl.document.ProcessedCorpus;
import net.benorourke.stocks.framework.thread.Result;

public class PreprocessingResult extends Result
{
    private final ProcessedCorpus corpus;

    public PreprocessingResult(ProcessedCorpus corpus)
    {
        this.corpus = corpus;
    }

    public ProcessedCorpus getCorpus()
    {
        return corpus;
    }

}
