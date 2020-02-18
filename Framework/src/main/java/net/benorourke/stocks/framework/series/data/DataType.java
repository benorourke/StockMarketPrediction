package net.benorourke.stocks.framework.series.data;

import net.benorourke.stocks.framework.persistence.gson.JsonAdapter;
import net.benorourke.stocks.framework.persistence.gson.data.DocumentAdapter;
import net.benorourke.stocks.framework.persistence.gson.data.StockQuoteAdapter;
import net.benorourke.stocks.framework.series.data.impl.Document;
import net.benorourke.stocks.framework.series.data.impl.ProcessedDocument;
import net.benorourke.stocks.framework.series.data.impl.ProcessedStockQuote;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;

public interface DataType<T extends Data>
{
    DataType<StockQuote> STOCK_QUOTE = new DataType<StockQuote>()
    {
        @Override
        public String getName()
        {
            return "Stock Quote";
        }

        @Override
        public JsonAdapter<StockQuote> getAdapter()
        {
            return new StockQuoteAdapter();
        }
    };
    DataType<Document> DOCUMENT = new DataType<Document>()
    {
        @Override
        public String getName()
        {
            return "Document";
        }

        @Override
        public JsonAdapter<Document> getAdapter()
        {
            return new DocumentAdapter();
        }
    };
    DataType<ProcessedStockQuote> PROCESSED_STOCK_QUOTE = new DataType<ProcessedStockQuote>()
    {
        @Override
        public String getName()
        {
            return "ProcessedStock Quote";
        }

        @Override
        public JsonAdapter<ProcessedStockQuote> getAdapter()
        {
            return null;
        }
    }; // TODO
    DataType<ProcessedDocument> PROCESSED_DOCUMENT = new DataType<ProcessedDocument>()
    {
        @Override
        public String getName()
        {
            return "Processed Document";
        }

        @Override
        public JsonAdapter<ProcessedDocument> getAdapter()
        {
            return null;
        }
    }; // TODO

    String getName();

    JsonAdapter<T> getAdapter();

}
