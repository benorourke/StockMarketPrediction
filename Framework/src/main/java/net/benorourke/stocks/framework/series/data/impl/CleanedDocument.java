package net.benorourke.stocks.framework.series.data.impl;

import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.DocumentType;
import net.benorourke.stocks.framework.series.data.ProcessedData;

import java.util.Date;
import java.util.List;

/**
 * TODO
 */
public class CleanedDocument extends ProcessedData
{
    private final String originalContent;
    private final List<String> cleanedTerms;
    private final DocumentType documentType;

    public CleanedDocument(Date date, String originalContent, List<String> cleanedTerms, DocumentType documentType)
    {
        super(DataType.CLEANED_DOCUMENT, date);

        this.originalContent = originalContent;
        this.cleanedTerms = cleanedTerms;
        this.documentType = documentType;
    }

    public String getOriginalContent()
    {
        return originalContent;
    }

    public List<String> getCleanedTerms()
    {
        return cleanedTerms;
    }

    public DocumentType getDocumentType()
    {
        return documentType;
    }

}
