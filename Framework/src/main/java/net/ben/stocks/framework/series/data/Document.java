package net.ben.stocks.framework.series.data;

import java.util.Date;

public class Document extends Data
{
    private final String content;

    public Document(Date date, String content)
    {
        super(DataType.DOCUMENT, date);

        this.content = content;
    }

    @Override
    public boolean isProcessed()
    {
        return false;
    }

    public String getContent()
    {
        return content;
    }
}
