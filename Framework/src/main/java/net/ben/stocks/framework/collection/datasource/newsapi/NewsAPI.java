package net.ben.stocks.framework.collection.datasource.newsapi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.ben.stocks.framework.Framework;
import net.ben.stocks.framework.collection.*;
import net.ben.stocks.framework.collection.datasource.DataSource;
import net.ben.stocks.framework.collection.constraint.Constraint;
import net.ben.stocks.framework.collection.constraint.MaximumAgeConstraint;
import net.ben.stocks.framework.collection.constraint.OrderingConstraint;
import net.ben.stocks.framework.collection.session.CollectionSession;
import net.ben.stocks.framework.exception.ConstraintException;
import net.ben.stocks.framework.series.data.Document;
import net.ben.stocks.framework.exception.FailedCollectionException;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class NewsAPI extends DataSource<Document>
{
    private static final String BASE_URL = "https://newsapi.org/";
    private static final int MAX_PAGE_SIZE = 100;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private final String apiKey;

    public NewsAPI(String apiKey)
    {
        this.apiKey = apiKey;
    }

    @Override
    public Class<? extends Document> getDataClass()
    {
        return Document.class;
    }

    @Override
    public Constraint[] getConstraints()
    {
        return new Constraint[]
                {
                        new OrderingConstraint(),
                        new MaximumAgeConstraint(28)
                };
    }

    @Override
    public CollectionSession newSession(Query completeQuery)
    {
        return new NewsAPICollectionSession(completeQuery);
    }

    @Override
    public ConnectionResponse<Document> retrieve(Query query)
            throws ConstraintException, FailedCollectionException
    {
        checkConstraints(query);

        // TODO CREATE AN OBJECT WITH A DIRECT JSON MAPPING - GSON CAN FREEZE IF THE RESPONSE
        // DOESN'T MATCH WHAT WE NEED (I.E. AN ERROR)

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
                List<Document> documents = parseDocuments(json);

                Framework.info("Documents parsed " + documents.size());
                ConnectionResponse<Document> response
                        = new ConnectionResponse<>(result, json, documents);
                return response;
            }
        }
        catch (IOException e)
        {
            throw new FailedCollectionException(e);
        }
    }

    private List<Document> parseDocuments(JsonObject json)
    {
        List<Document> documents = new ArrayList<Document>();
        JsonArray articles = json.getAsJsonArray("articles");

        for (JsonElement elem : articles)
        {
            JsonObject article = elem.getAsJsonObject();
            Date date = parseDate(article.getAsJsonPrimitive("publishedAt").getAsString());
            String content = article.get("title").getAsString();

            documents.add(new Document(date, content));
        }
        return documents;
    }

    /**
     * Example: "publishedAt": "2020-02-04T18:45:00Z"
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

    ///v2/everything?q=apple&from=2019-12-20&to=2019-12-20&sortBy=popularity&apiKey=78d93a9d68584e61be38b1d90217d1e7
    private String buildUrlExtension(Query query)
    {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(query.getFrom());
        String strFrom = calendar.get(Calendar.YEAR)
                                + "-" + (calendar.get(Calendar.MONTH) + 1)
                                + "-" + calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTime(query.getTo());
        String strTo = calendar.get(Calendar.YEAR)
                                + "-" + (calendar.get(Calendar.MONTH) + 1)
                                + "-" + calendar.get(Calendar.DAY_OF_MONTH);

        return "v2/everything"
                    .concat("?q=" + query.getStock().getCompanyName().replace(" ", "%20"))
                    .concat("&from=".concat(strFrom))
                    .concat("&to=".concat(strTo))
                    .concat("&sortBy=popularity")
                    .concat("&apiKey=" + apiKey)
                    .concat("&pageSize=" + MAX_PAGE_SIZE);
//        return "v2/top-headlines?country=gb&apiKey=" + apiKey;
    }


}
