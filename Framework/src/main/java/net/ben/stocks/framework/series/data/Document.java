package net.ben.stocks.framework.series.data;

import java.util.Date;

public class Document extends Data
{
    private final String content;

    public Document(Date date, String content)
    {
        super(date);

        this.content = content;
    }

    @Override
    public DataType getType()
    {
        return DataType.DOCUMENT;
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
