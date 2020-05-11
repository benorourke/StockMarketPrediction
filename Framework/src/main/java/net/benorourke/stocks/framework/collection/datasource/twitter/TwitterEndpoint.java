package net.benorourke.stocks.framework.collection.datasource.twitter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.collection.Query;
import net.benorourke.stocks.framework.collection.constraint.Constraint;
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
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * An abstract DataSource for the Official Twitter API Endpoint(s).
 *
 * Numerous endpoints can be used, which is specified in the constructor using {@link EndpointType}.
 */
public abstract class TwitterEndpoint extends DataSource<Document>
{
    /** The language to query for. */
    private static final String LANG = "en";
    /** The format to request data within a given timeframe in. */
    private static final DateFormat SINCE_UNTIL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    /** The format that dates of tweets in a TwitterAPI response are in. */
    private static final DateFormat RESPONSE_DATE_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");

    /** The type of endpoint for this instance. */
    private final EndpointType type;

    @CollectionVariable(name = "Consumer Key",
                        type = CollectionVariable.Type.STRING,
                        prompt = "API Key")
    private String apiKey;
    @CollectionVariable(name = "Consumer Secret",
                        type = CollectionVariable.Type.STRING,
                        prompt = "API Secret Key")
    private String apiSecret;
    @CollectionVariable(name = "Access Token",
                        type = CollectionVariable.Type.STRING,
                        prompt = "Access Token")
    private String accessToken;
    @CollectionVariable(name = "Access Token Secret",
                        type = CollectionVariable.Type.STRING,
                        prompt = "Access Token Secret")
    private String accessTokenSecret;
    @CollectionVariable(name = "Query",
                        type = CollectionVariable.Type.STRING,
                        prompt = "English Tweets Containing")
    private String query;
    @CollectionVariable(name = "Tweets per Day",
                        type = CollectionVariable.Type.INTEGER,
                        prompt = "Max 100")
    private int elementsPerDay;

    /**
     * Create a new instance.
     *
     * @param type the endpoint to use for this instance
     */
    public TwitterEndpoint(EndpointType type)
    {
        super("Twitter: " + type.getLocale());

        this.type = type;
        this.elementsPerDay = 100;
    }

    /**
     * Get the base URL of this endpoint.
     *
     * @return the base URL
     */
    public abstract String getBaseUrl();

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
        // If the endpoint does not have access to the full archive, we must apply the age constraint
        return type.getAgeConstraint() == null
                    ? new Constraint[0]
                    : new Constraint[] { type.getAgeConstraint() };
    }

    @Override
    public DailyCollectionSession<Document> newSession(Query completeQuery, CollectionFilter<Document> collectionFilter)
    {
        return new DailyCollectionSession(completeQuery, collectionFilter);
    }

    @Override
    public CollectionFilter<Document> newDefaultCollectionFilter()
    {
        /**
         * No default collection filter - Twitter will allow for complex search results containing the search term
         * we requested so we might as well use this.
         */
        return data -> false;
    }

    @Override
    public Collection<Document> retrieve(Query query) throws ConstraintException, FailedCollectionException
    {
        checkConstraintsOrThrow(query);

        // Request OAuth authentication using the details provided
        OAuthConsumer consumer = new CommonsHttpOAuthConsumer(apiKey, apiSecret);
        consumer.setTokenWithSecret(accessToken, accessTokenSecret);

        // Create the URL Request for the endpoint that data will be retrieved from
        String since = SINCE_UNTIL_DATE_FORMAT.format(query.getFrom());
        String until = SINCE_UNTIL_DATE_FORMAT.format(query.getTo());
        String url = getBaseUrl() + buildUrlExtension(since, until, elementsPerDay);
        Framework.info("Connecting to " + url);

        List<Document> result = new ArrayList<>();
        try
        {
            HttpGet request = new HttpGet(url);
            // Sign the GET request with the authentication
            consumer.sign(request);
            HttpClient client = new DefaultHttpClient();
            // Get the response
            HttpResponse response = client.execute(request);
            String responseToString = EntityUtils.toString(response.getEntity(), "UTF-8");

            // Parse the response using JSON
            JsonObject json = new Gson().fromJson(responseToString, JsonObject.class);
            JsonArray tweets = json.getAsJsonArray("statuses");
            Framework.info("Number of tweets received: " + tweets.size());

            // Populate the response with the deserialized tweets
            for (JsonElement element : tweets)
            {
                JsonObject obj = element.getAsJsonObject();
                String createdAt = obj.get("created_at").getAsString();
                String tweet = obj.get("text").getAsString();

                Date date = RESPONSE_DATE_FORMAT.parse(createdAt);
                result.add(new Document(date, tweet, DocumentType.TWEET));
            }

        }
        catch (Exception e)
        {
            throw new FailedCollectionException(this, FailedCollectionException.Type.HTTP_ERROR,
                                                "Unable to collect from " + url);
        }

        return result;
    }

    /**
     * Create the URL extension parameters to connect to.
     *
     * @param from when to collect tweets from
     * @param until when to collect tweets until
     * @param count the number of tweets to connect
     * @return the URL extension parameters
     */
    private String buildUrlExtension(String from, String until, int count)
    {
        return ("?q=" + StringUtil.encodeUrlParam(query))
//                    .concat("&from=".concat(StringUtil.encodeUrlParam(from)))
//                    .concat("&until=".concat(StringUtil.encodeUrlParam(until)))
                    .concat("&lang=".concat(LANG))
                    .concat("&result_type=mixed")
                    .concat("&count=" + count);
    }

}
