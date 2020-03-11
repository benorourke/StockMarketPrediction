package net.benorourke.stocks.framework.series.data.impl;

import net.benorourke.stocks.framework.preprocess.document.Sentiment;
import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.DataType;

import java.util.Date;

public class ProcessedDocument extends Data
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
