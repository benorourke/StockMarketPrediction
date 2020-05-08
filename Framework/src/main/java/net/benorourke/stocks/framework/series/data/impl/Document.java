package net.benorourke.stocks.framework.series.data.impl;

import net.benorourke.stocks.framework.series.data.*;

import java.util.Date;
import java.util.UUID;

public class Document extends Data implements IdentifiableData
{
    /**
     * A unique ID to distinguish raw quotes from one another efficiently.
     *
     * Used to remove raw data.
     */
    private final UUID id;

    @RawDataElementAnnotation
    private final String content;
    @RawDataElementAnnotation
    private final DocumentType documentType;

    public Document(UUID id, Date date, String content, DocumentType documentType)
    {
        super (DataType.DOCUMENT, date);

        this.id = id;
        this.content = content;
        this.documentType = documentType;
    }

    /**
     * The constructor used when injecting Documents dynamically through the UI.
     *
     * @param date
     * @param content
     * @param documentType
     */
    @RawDataAnnotation(indexOfDate = 0, paramOrder = {"content", "documentType"})
    public Document(Date date, String content, DocumentType documentType)
    {
        this (UUID.randomUUID(), date, content, documentType);
    }

    @Override
    public boolean isDuplicate(Data other)
    {
        if (!(other instanceof Document))
            return false;

        Document dOther = (Document) other;
        return content.toLowerCase().equalsIgnoreCase(dOther.content.toLowerCase());
    }

    @Override
    public UUID getId()
    {
        return id;
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
