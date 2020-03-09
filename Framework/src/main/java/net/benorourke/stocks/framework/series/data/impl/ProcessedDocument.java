package net.benorourke.stocks.framework.series.data.impl;

import net.benorourke.stocks.framework.preprocess.impl.document.Sentiment;
import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.ProcessedData;

import java.util.Date;

public class ProcessedDocument extends ProcessedData
{
    private final Sentiment sentiment;

    public ProcessedDocument(Date date, Sentiment sentiment)
    {
        super(DataType.PROCESSED_DOCUMENT, date);

        this.sentiment = sentiment;
    }

    public Sentiment getSentiment()
    {
        return sentiment;
    }

}
