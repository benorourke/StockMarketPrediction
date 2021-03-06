package net.benorourke.stocks.framework.collection.datasource.newsapi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.collection.Query;
import net.benorourke.stocks.framework.collection.URLConnector;
import net.benorourke.stocks.framework.collection.constraint.Constraint;
import net.benorourke.stocks.framework.collection.constraint.MaximumAgeConstraint;
import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.collection.datasource.variable.CollectionVariable;
import net.benorourke.stocks.framework.collection.session.DailyCollectionSession;
import net.benorourke.stocks.framework.collection.session.filter.CollectionFilter;
import net.benorourke.stocks.framework.exception.ConstraintException;
import net.benorourke.stocks.framework.exception.FailedCollectionException;
import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.DocumentType;
import net.benorourke.stocks.framework.series.data.impl.Document;
import net.benorourke.stocks.framework.util.StringUtil;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The DataSource for the News API.
 */
public class NewsAPI extends DataSource<Document>
{
    private static final String BASE_URL = "https://newsapi.org/";
    /** The format of the publishing dates of Documents in News API responses */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @CollectionVariable(name = "API Key",
                        type = CollectionVariable.Type.STRING,
                        prompt = "NewsAPI API Key",
                        validators = {"ALPHANUMERIC"})
    private String apiKey;
    @CollectionVariable(name = "Search Term",
                        type = CollectionVariable.Type.STRING,
                        prompt = "Headlines Containing")
    private String searchTerm;
    @CollectionVariable(name = "Headlines per Day",
                        type = CollectionVariable.Type.INTEGER,
                        prompt = "Max 100")
    private int elementsPerDay;

    /**
     * Create a new instance.
     */
    public NewsAPI()
    {
        super("NewsAPI");

        this.elementsPerDay = 100;
    }

    @Override
    public Class<? extends Document> getDataClass()
    {
        return Document.class;
    }

    @Override
    public DataType getDataType()
    {
        return DataType.DOCUMENT;
    }

    @Override
    public Constraint[] getConstraints()
    {
        return new Constraint[]
        {
                // NewsAPI only allows you to collect up to 28 days worth of news
                new MaximumAgeConstraint(28)
        };
    }

    @Override
    public DailyCollectionSession<Document> newSession(Query completeQuery, CollectionFilter<Document> collectionFilter)
    {
        return new DailyCollectionSession(completeQuery, collectionFilter);
    }

    @Override
    public CollectionFilter<Document> newDefaultCollectionFilter()
    {
        // Filter any data that doesn't contain the news we want
//        return data -> !data.getContent().toLowerCase().contains(searchTerm.toLowerCase());
        return data -> false;
    }

    @Override
    public Collection<Document> retrieve(Query query) throws ConstraintException, FailedCollectionException
    {
        checkConstraintsOrThrow(query);

        try
        {
            // Connect to the URL
            String url = BASE_URL.concat(buildUrlExtension(query));
            Framework.info("Connecting to " + url);
            URLConnector connector = URLConnector.connect(url);
            String result = connector.read();

            if(connector.getResponseCode() != URLConnector.RESPONSE_OK)
            {
                throw new FailedCollectionException(this, connector.getResponseCode());
            }
            else
            {
                JsonObject json = new Gson().fromJson(result, JsonObject.class);
                return parseDocuments(json);
            }
        }
        catch (IOException e)
        {
            throw new FailedCollectionException(this, e);
        }
    }

    private List<Document> parseDocuments(JsonObject json)
    {
        List<Document> documents = new ArrayList<Document>();
        JsonArray articles = json.getAsJsonArray("articles");

        // Parse the data received and return a list of deserialized objects
        for (JsonElement elem : articles)
        {
            JsonObject article = elem.getAsJsonObject();
            Date date = parseDate(article.getAsJsonPrimitive("publishedAt").getAsString());
            String content = article.get("title").getAsString();

            documents.add(new Document(date, content, DocumentType.NEWS_HEADLINE));
        }
        return documents;
    }

    /**
     * Example: "publishedAt": "2020-02-04T18:45:00Z".
     *
     * @param strDate the string representation of the Date
     * @return the parsed Date
     */
    private Date parseDate(String strDate)
    {
        try
        {
            Date date = DATE_FORMAT.parse(strDate);
            return date;
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
     * @param query the time frame to collect data between
     * @return the URL to connect to
     */
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
                    .concat("?q=" + StringUtil.encodeUrlParam(searchTerm))
                    .concat("&from=".concat(strFrom))
                    .concat("&to=".concat(strTo))
                    .concat("&sortBy=popularity")
                    .concat("&apiKey=" + apiKey)
                    .concat("&pageSize=" + elementsPerDay);
//        return "v2/top-headlines?country=gb&apiKey=" + apiKey;
    }

}
