package net.ben.stocks.framework.data;

import java.util.Date;

/**
 * TODO Finish class & add processing
 */
public class ProcessedDocument extends Document
{

    public ProcessedDocument(Date date, String content)
    {
        super(date, content);
    }

    @Override
    public boolean isProcessed()
    {
        return true;
    }

}
