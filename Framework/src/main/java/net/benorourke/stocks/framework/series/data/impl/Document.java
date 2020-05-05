package net.benorourke.stocks.framework.series.data.impl;

import net.benorourke.stocks.framework.series.data.*;

import java.util.Date;

public class Document extends Data
{
    @RawDataElementAnnotation
    private final String content;
    @RawDataElementAnnotation
    private final DocumentType documentType;

    @RawDataAnnotation(indexOfDate = 0, paramOrder = {"content", "documentType"})
    public Document(Date date, String content, DocumentType documentType)
    {
        super(DataType.DOCUMENT, date);

        this.content = content;
        this.documentType = documentType;
    }

    @Override
    public boolean isDuplicate(Data other)
    {
        if (!(other instanceof Document))
            return false;

        Document dOther = (Document) other;
        return content.toLowerCase().equalsIgnoreCase(dOther.content.toLowerCase());
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
