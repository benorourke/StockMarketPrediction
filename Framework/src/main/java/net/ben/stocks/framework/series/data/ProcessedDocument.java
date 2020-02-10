package net.ben.stocks.framework.series.data;

import java.util.Date;

/**
 * TODO
 */
public class ProcessedDocument extends Data
{
    private final String content;

    public ProcessedDocument(Date date, String content)
    {
        super(DataType.PROCESSED_DOCUMENT, date);

        this.content = content;
    }

    @Override
    public boolean isProcessed()
    {
        return true;
    }

    public String getContent()
    {
        return content;
    }
}
