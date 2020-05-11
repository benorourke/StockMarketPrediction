package net.benorourke.stocks.framework.series.data;

import net.benorourke.stocks.framework.series.data.impl.CleanedDocument;
import net.benorourke.stocks.framework.series.data.impl.Document;
import net.benorourke.stocks.framework.series.data.impl.ProcessedDocument;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;

/**
 * Interface rather than enum so custom DataTypes can be injected at runtime by future users.
 *
 * @param <T> the type for this datatype.
 */
public interface DataType<T extends Data>
{
    DataType<StockQuote> STOCK_QUOTE = () -> "Stock Quote";
    DataType<Document> DOCUMENT = () -> "Document";
    DataType<CleanedDocument> CLEANED_DOCUMENT = () -> "Cleaned Document";
    DataType<ProcessedDocument> PROCESSED_DOCUMENT = () -> "Processed Document";

    String getName();

}
