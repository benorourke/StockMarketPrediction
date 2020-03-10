package net.benorourke.stocks.framework.preprocess.document.relevancy;

import net.benorourke.stocks.framework.series.data.impl.CleanedDocument;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * A metric for producing the top terms.
 */
public interface RelevancyMetric
{

    void initialise(Map<Date, List<CleanedDocument>> data);

    /**
     *
     * @param maximumCount -> maximum because cardinality of return could be less
     * @return
     */
    String[] getMostRelevant(int maximumCount);

}
