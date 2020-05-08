package net.benorourke.stocks.framework.collection.datasource.twitter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.collection.Query;
import net.benorourke.stocks.framework.collection.constraint.Constraint;
import net.benorourke.stocks.framework.collection.constraint.OrderingConstraint;
import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.collection.datasource.variable.CollectionVariable;
import net.benorourke.stocks.framework.collection.session.APICollectionSession;
import net.benorourke.stocks.framework.collection.session.filter.CollectionFilter;
import net.benorourke.stocks.framework.exception.ConstraintException;
import net.benorourke.stocks.framework.exception.FailedCollectionException;
import net.benorourke.stocks.framework.series.data.DataType;
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

public abstract class TwitterEndpoint extends DataSource<Document>
{
    private static final String LANG = "en";
    private static final DateFormat SINCE_UNTIL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @CollectionVariable(name = "Consumer Key",
                        type = CollectionVariable.Type.STRING,
                        prompt = "API Key")
    private String consumerKey;
    @CollectionVariable(name = "Consumer Secret",
                        type = CollectionVariable.Type.STRING,
                        prompt = "API Secret Key")
    private String consumerSecret;
    @CollectionVariable(name = "Query",
                        type = CollectionVariable.Type.STRING,
                        prompt = "English Tweets Containing")
    private String query;
    @CollectionVariable(name = "Tweets per Day",
                        type = CollectionVariable.Type.INTEGER,
                        prompt = "Number of Tweets per Day",
                        validators = {})
    private int elementsPerDay;

    public static final String apiKey = "F5eGt12ivPWYFDvPTSgjCy60M";
    public static final String apiSecret = "WtSjuel0VsK03hbBDP9gEzIXNPsuRp0RFUtmYF180atfu4jZPl";
    public static final String accessToken = "1257440547033284608-ITOKERP6rRTaZK72lfjkvySMyEbSyD";
    public static final String accessTokenSecret = "bXNyHNaNxcV6sGySvwbHsFFmM9jHRBDr9hAiG9G4Q8uc3";

    public TwitterEndpoint(EndpointType type)
    {
        super("Twitter: " + type.getLocale());

        this.elementsPerDay = 100;
    }

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
        return new Constraint[]
        {
                new OrderingConstraint(),
        };
    }

    @Override
    public APICollectionSession<Document> newSession(Query completeQuery, CollectionFilter<Document> collectionFilter)
    {
        return new TwitterCollectionSession(completeQuery, collectionFilter);
    }

    @Override
    public CollectionFilter<Document> newDefaultCollectionFilter()
    {
        /**
         * No default collection filter - Twitter will allow for complex search results that may not necessarily be
         * in the order of what we searched.
         */
        return data -> false;
    }

    // TODO Remove
    public static boolean done = false;

    @Override
    public Collection<Document> retrieve(Query query) throws ConstraintException, FailedCollectionException
    {
        checkConstraintsOrThrow(query);

        if (done)
            return null;

        done = true;

        Framework.debug("1");

        OAuthConsumer consumer = new CommonsHttpOAuthConsumer(apiKey, apiSecret);
        consumer.setTokenWithSecret(accessToken, accessTokenSecret);
        Framework.debug("2");

         // TODO: Count iterator of some sorts (i.e. exceed 100)

        String since = SINCE_UNTIL_DATE_FORMAT.format(query.getFrom());
        String until = SINCE_UNTIL_DATE_FORMAT.format(query.getTo());

        String url = getBaseUrl() + buildUrlExtension(since, until, 100);
        HttpGet request = new HttpGet(url);

        Framework.debug("3");
        try
        {
            consumer.sign(request);
            Framework.debug("4");

            HttpClient client = new DefaultHttpClient();
            Framework.debug("5");
            HttpResponse response = client.execute(request);
            Framework.debug("6");

            String responseToString = EntityUtils.toString(response.getEntity(), "UTF-8");

            Framework.info(responseToString);

            Framework.debug("7");
            JsonObject json = new Gson().fromJson(responseToString, JsonObject.class);
            Framework.debug("8");

            JsonArray tweets = json.getAsJsonArray("statuses");
            Framework.debug("Number of tweets received: " + tweets.size());

            int ind = 0;
            for (JsonElement element : tweets)
            {
                JsonObject obj = element.getAsJsonObject();

                Framework.debug((ind ++) + ": ");
                String createdAt = obj.get("created_at").getAsString();
                Framework.debug("    Created @ " + createdAt);
                String tweet = obj.get("text").getAsString();
                Framework.debug("    Text: " + tweet);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return new ArrayList();
    }

    private String buildUrlExtension(String from, String until, int count)
    {
        return ("?q=" + StringUtil.encodeUrlParam(query))
//                    .concat("&from=".concat(StringUtil.encodeUrlParam(from)))
//                    .concat("&until=".concat(StringUtil.encodeUrlParam(until)))
                    .concat("&result_type=mixed")
                    .concat("&count=" + count);
    }

}