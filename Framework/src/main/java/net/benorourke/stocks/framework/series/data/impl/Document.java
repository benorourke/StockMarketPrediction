package net.benorourke.stocks.framework.series.data.impl;

import net.benorourke.stocks.framework.series.data.*;

import java.util.Date;
import java.util.UUID;

/**
 * A textual piece of data.
 */
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

    /**
     * Create a new instance.
     *
     * @param id the unique identifier for this document.
     * @param date the date this document was created
     * @param content the textual data of the document
     * @param documentType the type of the document
     */
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
     * @param date the date this document was created
     * @param content the textual data of the document
     * @param documentType the type of the document
     */
    @RawDataConstructorAnnotation(indexOfDate = 0, paramOrder = {"content", "documentType"})
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
