package net.benorourke.stocks.framework.collection.datasource.alphavantage;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.collection.Query;
import net.benorourke.stocks.framework.collection.URLConnector;
import net.benorourke.stocks.framework.collection.constraint.Constraint;
import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.collection.datasource.variable.CollectionVariable;
import net.benorourke.stocks.framework.collection.session.CollectionSession;
import net.benorourke.stocks.framework.collection.session.filter.CollectionFilter;
import net.benorourke.stocks.framework.exception.ConstraintException;
import net.benorourke.stocks.framework.exception.FailedCollectionException;
import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;
import net.benorourke.stocks.framework.util.DateUtil;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * The DataSource for the AlphaVantage StockQuote API.
 */
public class AlphaVantage extends DataSource<StockQuote>
{
    private static final String BASE_URL = "https://www.alphavantage.co/";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @CollectionVariable(name = "API Key",
                        type = CollectionVariable.Type.STRING,
                        prompt = "AlphaVantage API Key",
                        validators = {"ALPHANUMERIC"})
    private String apiKey;
    @CollectionVariable(name = "Stock Symbol",
                        type = CollectionVariable.Type.STRING,
                        prompt = "Alpha Vantage Symbol (e.g. LON:VOD)",
                        validators = {})
    private String symbol;

    /**
     * Create a new instance.
     */
    public AlphaVantage()
    {
        super("AlphaVantage");
    }

    @Override
    public Class<? extends StockQuote> getDataClass()
    {
        return StockQuote.class;
    }

    @Override
    public DataType getDataType()
    {
        return DataType.STOCK_QUOTE;
    }

    @Override
    public Constraint[] getConstraints()
    {
        return new Constraint[0];
    }

    @Override
    public CollectionSession<StockQuote> newSession(Query completeQuery, CollectionFilter<StockQuote> collectionFilter)
    {
        return new AlphaVantageCollectionSession(completeQuery, collectionFilter);
    }

    @Override
    public CollectionFilter<StockQuote> newDefaultCollectionFilter()
    {
        // No filter required
        return data -> false;
    }

    @Override
    public Collection<StockQuote> retrieve(Query query) throws FailedCollectionException, ConstraintException
    {
        checkConstraintsOrThrow(query);

        try
        {
            // Connect to the URL
            String url = BASE_URL.concat(buildUrlExtension());
            Framework.info("Connecting to " + url);
            URLConnector connector = URLConnector.connect(url);
            String result = connector.read();

            if(connector.getResponseCode() != URLConnector.RESPONSE_OK)
            {
                throw new FailedCollectionException(this, connector.getResponseCode());
            }
            else
            {
                // Attempt to parse the respone object
                JsonObject json = new Gson().fromJson(result, JsonObject.class);
                return parseQuotes(query, json);
            }
        }
        catch (IOException e)
        {
            throw new FailedCollectionException(this, e);
        }
    }

    private List<StockQuote> parseQuotes(Query query, JsonObject json)
    {
        List<StockQuote> quotes = new ArrayList<>();
        // The key Time Series (Daily) contains the JSON array of all
        JsonObject dataset = json.getAsJsonObject("Time Series (Daily)");

        Framework.info("Total unfiltered stock quotes received " + dataset.size());

        long earliestDate = query.getFrom().getTime();
        long latestDate = query.getTo().getTime();
        for (String key : dataset.keySet())
        {
            Date date = parseDate(key);
            /**
             * In AlphaVantage you can't specify dates, but instead have to use outputsize=full,
             * which then we can filter here based on
             */
            if(date.getTime() > earliestDate && date.getTime() < latestDate
                    || DateUtil.sameDay(query.getFrom(), date)
                    || DateUtil.sameDay(query.getTo(), date))
            {
                JsonObject datapoint = dataset.getAsJsonObject(key);
                double open = datapoint.getAsJsonPrimitive("1. open").getAsDouble();
                double high = datapoint.getAsJsonPrimitive("2. high").getAsDouble();
                double low = datapoint.getAsJsonPrimitive("3. low").getAsDouble();
                double close = datapoint.getAsJsonPrimitive("4. close").getAsDouble();
                long volume = datapoint.getAsJsonPrimitive("5. volume").getAsLong();
                quotes.add(new StockQuote(date, open, close, high, low, volume));
            }
        }
        return quotes;
    }

    /**
     * Example: 2020-02-07
     *
     * @param strDate the string representation of the Date
     * @return the parsed Date
     */
    private Date parseDate(String strDate)
    {
        try
        {
            return DATE_FORMAT.parse(strDate);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Build the URL based on the current collection variables.
     *
     * @return the URL to connect to
     */
    private String buildUrlExtension()
    {
        return "query"
                .concat("?function=TIME_SERIES_DAILY")
                .concat("&symbol=".concat(symbol))
                .concat("&outputsize=full")
                .concat("&apikey=".concat(apiKey));
    }

}
