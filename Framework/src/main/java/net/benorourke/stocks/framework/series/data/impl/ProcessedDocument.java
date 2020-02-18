package net.benorourke.stocks.framework.series.data.impl;

import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.DocumentType;
import net.benorourke.stocks.framework.series.data.ProcessedData;

import java.util.Date;

/**
 * TODO
 */
public class ProcessedDocument extends ProcessedData
{
    private final String content;
    private final DocumentType documentType;

    public ProcessedDocument(Date date, String content, DocumentType documentType)
    {
        super(DataType.PROCESSED_DOCUMENT, date);

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
