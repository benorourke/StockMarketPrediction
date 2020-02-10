package net.ben.stocks.framework.collection.datasource.alphavantage;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.ben.stocks.framework.Framework;
import net.ben.stocks.framework.collection.ConnectionResponse;
import net.ben.stocks.framework.collection.URLConnector;
import net.ben.stocks.framework.collection.session.CollectionSession;
import net.ben.stocks.framework.collection.constraint.Constraint;
import net.ben.stocks.framework.collection.datasource.DataSource;
import net.ben.stocks.framework.collection.Query;
import net.ben.stocks.framework.collection.constraint.OrderingConstraint;
import net.ben.stocks.framework.exception.ConstraintException;
import net.ben.stocks.framework.series.data.StockQuote;
import net.ben.stocks.framework.exception.FailedCollectionException;
import net.ben.stocks.framework.util.DateUtil;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlphaVantage extends DataSource<StockQuote>
{
    private static final String BASE_URL = "https://www.alphavantage.co/";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final String apiKey;

    public AlphaVantage(String apiKey)
    {
        this.apiKey = apiKey;
    }

    @Override
    public Class<? extends StockQuote> getDataClass()
    {
        return StockQuote.class;
    }

    @Override
    public Constraint[] getConstraints()
    {
        return new Constraint[]
                {
                        new OrderingConstraint()
                };
    }

    @Override
    public CollectionSession newSession(Query completeQuery)
    {
        return null;
    }

    @Override
    public ConnectionResponse<StockQuote> retrieve(Query query)
            throws FailedCollectionException, ConstraintException
    {
        checkConstraints(query);

        try
        {
            String url = BASE_URL.concat(buildUrlExtension(query));
            Framework.info("Connecting to " + url);

            URLConnector connector = URLConnector.connect(url);
            String result = connector.read();

            if(connector.getResponseCode() != URLConnector.RESPONSE_OK)
            {
                throw new FailedCollectionException(connector.getResponseCode());
            }
            else
            {
                JsonObject json = new Gson().fromJson(result, JsonObject.class);
                List<StockQuote> quotes = parseQuotes(query, json);

                Framework.info("Documents parsed " + quotes.size());
                ConnectionResponse<StockQuote> response
                        = new ConnectionResponse<>(result, json, quotes);
                return response;
            }
        }
        catch (IOException e)
        {
            throw new FailedCollectionException(e);
        }
    }

    private List<StockQuote> parseQuotes(Query query, JsonObject json)
    {
        // TODO CREATE AN OBJECT WITH A DIRECT JSON MAPPING - GSON CAN FREEZE IF THE RESPONSE
        // DOESN'T MATCH WHAT WE NEED (I.E. AN ERROR)

        List<StockQuote> quotes = new ArrayList<StockQuote>();
        JsonObject dataset = json.getAsJsonObject("Time Series (Daily)");

        Framework.info("Total unfiltered stock quotes received " + dataset.size());

        long earliestDate = query.getFrom().getTime();
        long latestDate = query.getTo().getTime();
        for (String key : dataset.keySet())
        {
            Date date = parseDate(key);
            /**
             * In AlphaVantage you can't specify dates, but instead have to use outputsize=full,
             * which you can then filter dates based on
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
     * @param date
     * @return
     */
    private Date parseDate(String date)
    {
        try
        {
            return DATE_FORMAT.parse(date);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private String buildUrlExtension(Query query)
    {
        //https://www.alphavantage.co/query?function=TIME_SERIES_WEEKLY&symbol=LON:VOD&apikey=ZJULNKK5LP9TFN4P
        return "query"
                .concat("?function=TIME_SERIES_DAILY")
                .concat("&symbol=LON:VOD") // TODO - make symbol resolving automatic
                .concat("&outputsize=full")
                .concat("&apikey=".concat(apiKey));
    }

}
