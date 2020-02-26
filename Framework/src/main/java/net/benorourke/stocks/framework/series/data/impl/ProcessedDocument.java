package net.benorourke.stocks.framework.series.data.impl;

import net.benorourke.stocks.framework.preprocess.impl.document.Sentiment;
import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.ProcessedData;

import java.util.Date;

public class ProcessedDocument extends ProcessedData
{
    private String cleanedText;
    private Sentiment sentiment;
    private double bagOfWordsScore;

    public ProcessedDocument(Date date)
    {
        super(DataType.PROCESSED_DOCUMENT, date);
    }

}
