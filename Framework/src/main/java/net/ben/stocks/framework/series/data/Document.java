package net.ben.stocks.framework.series.data;

import java.util.Date;

public class Document extends Data
{
    private final String content;
    private final DocumentType documentType;

    public Document(Date date, String content, DocumentType documentType)
    {
        super(DataType.DOCUMENT, date);

        this.content = content;
        this.documentType = documentType;
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

    public DocumentType getDocumentType()
    {
        return documentType;
    }

}
