package net.benorourke.stocks.framework.preprocess.impl.document;

import net.benorourke.stocks.framework.series.data.impl.ProcessedDocument;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ProcessedCorpus
{
    private Map<Date, List<ProcessedDocument>> documents;

    public ProcessedCorpus(Map<Date, List<ProcessedDocument>> documents)
    {
        this.documents = documents;
    }

    public Map<Date, List<ProcessedDocument>> getDocuments()
    {
        return documents;
    }

}
