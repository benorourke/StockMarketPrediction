package net.benorourke.stocks.framework.series.data.impl;

import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.DocumentType;

import java.util.Date;
import java.util.List;

public class CleanedDocument extends Data
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

    @Override
    public boolean isDuplicate(Data other)
    {
        if (!(other instanceof Document))
            return false;

        CleanedDocument cdOther = (CleanedDocument) other;
        return originalContent.toLowerCase().equalsIgnoreCase(cdOther.originalContent.toLowerCase());
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
