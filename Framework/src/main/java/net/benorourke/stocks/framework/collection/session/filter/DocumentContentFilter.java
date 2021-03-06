package net.benorourke.stocks.framework.collection.session.filter;

import net.benorourke.stocks.framework.series.data.impl.Document;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A collection filter that will remove any documents that do not contain a single element in a list of strings.
 */
public class DocumentContentFilter implements CollectionFilter<Document>
{
    private final List<String> searchFor;
    private final boolean caseInsensitive;

    /**
     * Create a new instance.
     *
     * @param searchFor the string to search for
     * @param caseInsensitive whether to search case insensitively
     */
    public DocumentContentFilter(List<String> searchFor, boolean caseInsensitive)
    {
        if (caseInsensitive)
            this.searchFor = Collections.unmodifiableList(searchFor.stream()
                                                                   .map(String::toLowerCase)
                                                                   .collect(Collectors.toList()));
        else
            this.searchFor = Collections.unmodifiableList(searchFor);

        this.caseInsensitive = caseInsensitive;
    }

    @Override
    public boolean discard(Document data)
    {
        String content = (caseInsensitive)
                                ? data.getContent().toLowerCase()
                                : data.getContent();

        for (String term : searchFor)
        {
            if (content.contains(term))
                return false;
        }
        return true;
    }

}
