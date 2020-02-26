package net.benorourke.stocks.framework.series.data.impl;

import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.DocumentType;
import net.benorourke.stocks.framework.series.data.ProcessedData;

import java.util.Date;

/**
 * TODO
 */
public class CleanedDocument extends ProcessedData
{
    private final String cleanedContent;
    private final DocumentType documentType;

    public CleanedDocument(Date date, String content, DocumentType documentType)
    {
        super(DataType.CLEANED_DOCUMENT, date);

        this.cleanedContent = content;
        this.documentType = documentType;
    }

    public String getCleanedContent()
    {
        return cleanedContent;
    }

    public DocumentType getDocumentType()
    {
        return documentType;
    }
}
