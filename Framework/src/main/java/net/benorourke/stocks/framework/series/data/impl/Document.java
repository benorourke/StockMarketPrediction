package net.benorourke.stocks.framework.series.data.impl;

import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.DocumentType;

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

    public String getContent()
    {
        return content;
    }

    public DocumentType getDocumentType()
    {
        return documentType;
    }

}
