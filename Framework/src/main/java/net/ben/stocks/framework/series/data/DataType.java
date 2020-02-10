package net.ben.stocks.framework.series.data;

import net.ben.stocks.framework.persistence.gson.JsonAdapter;
import net.ben.stocks.framework.persistence.gson.data.DocumentAdapter;
import net.ben.stocks.framework.persistence.gson.data.StockQuoteAdapter;

public interface DataType<T extends Data>
{
    DataType<StockQuote> STOCK_QUOTE = new DataType<StockQuote>()
    {
        @Override
        public JsonAdapter<StockQuote> getAdapter()
        {
            return new StockQuoteAdapter();
        }
    };
    DataType<Document> DOCUMENT = new DataType<Document>()
    {
        @Override
        public JsonAdapter<Document> getAdapter()
        {
            return new DocumentAdapter();
        }
    };
    DataType<ProcessedStockQuote> PROCESSED_STOCK_QUOTE = new DataType<ProcessedStockQuote>()
    {
        @Override
        public JsonAdapter<ProcessedStockQuote> getAdapter()
        {
            return null;
        }
    }; // TODO
    DataType<ProcessedDocument> PROCESSED_DOCUMENT = new DataType<ProcessedDocument>()
    {
        @Override
        public JsonAdapter<ProcessedDocument> getAdapter()
        {
            return null;
        }
    }; // TODO

    JsonAdapter<T> getAdapter();

}
