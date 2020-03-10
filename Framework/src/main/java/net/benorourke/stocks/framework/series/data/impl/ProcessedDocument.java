package net.benorourke.stocks.framework.series.data.impl;

import net.benorourke.stocks.framework.preprocess.document.Sentiment;
import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.ProcessedData;

import java.util.Date;

public class ProcessedDocument extends ProcessedData
{
    private final Sentiment sentiment;
    private final boolean[] topTermVector;

    public ProcessedDocument(Date date, Sentiment sentiment, boolean[] topTermVector)
    {
        super(DataType.PROCESSED_DOCUMENT, date);

        this.sentiment = sentiment;
        this.topTermVector = topTermVector;
    }

    public Sentiment getSentiment()
    {
        return sentiment;
    }

    public boolean[] getTopTermVector()
    {
        return topTermVector;
    }

}
