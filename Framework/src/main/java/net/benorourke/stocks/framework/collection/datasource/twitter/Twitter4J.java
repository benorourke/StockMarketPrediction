package net.benorourke.stocks.framework.collection.datasource.twitter;

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
import net.benorourke.stocks.framework.series.data.DocumentType;
import net.benorourke.stocks.framework.series.data.impl.Document;
import twitter4j.*;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class Twitter4J extends DataSource<Document>
{
    private static final DateFormat SINCE_UNTIL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @CollectionVariable(name = "Consumer Key",
                        type = CollectionVariable.Type.STRING,
                        prompt = "API Key")
    private String consumerKey;
    @CollectionVariable(name = "Consumer Secret",
                        type = CollectionVariable.Type.STRING,
                        prompt = "API Secret Key")
    private String consumerSecret;

    @CollectionVariable(name = "Search Term",
                        type = CollectionVariable.Type.STRING,
                        prompt = "Tweets Containing",
                        validators = {"ALPHANUMERIC"})
    private String searchTerm;
    @CollectionVariable(name = "Tweets per Day",
                        type = CollectionVariable.Type.INTEGER,
                        prompt = "Number of Tweets per Day",
                        validators = {})
    private int elementsPerDay;

    public Twitter4J()
    {
        super("Twitter");

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
                new OrderingConstraint(),
        };
    }

    /**
     * {@link APICollectionSession#nextQuery()} must return an instance of of {@link Twitter4JQuery}.
     *
     * @param completeQuery
     * @param collectionFilter
     * @return
     */
    @Override
    public APICollectionSession<Document> newSession(Query completeQuery, CollectionFilter<Document> collectionFilter)
    {
        return new Twitter4JCollectionSession(newTwitterInstance(), completeQuery, collectionFilter);
    }

    @Override
    public CollectionFilter<Document> newDefaultCollectionFilter()
    {
        return data -> !data.getContent().toLowerCase().contains(searchTerm.toLowerCase());
    }

    /**
     *
     * @param query an instance of {@link Twitter4JQuery}.
     *
     * @return
     * @throws ConstraintException
     * @throws FailedCollectionException
     */
    @Override
    public Collection<Document> retrieve(Query query) throws ConstraintException, FailedCollectionException
    {
        checkConstraintsOrThrow(query);

        Twitter4JQuery tQuery = (Twitter4JQuery) query;
        Twitter twitter = tQuery.getTwitter();

        try
        {
            Map<String, RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus("search");
            for (Map.Entry<String, RateLimitStatus> entry : rateLimitStatus.entrySet())
            {
                Framework.info(entry.getKey() + ": " + entry.getValue().getRemaining());
            }
        }
        catch (TwitterException e)
        {
            e.printStackTrace();
        }

        String since = SINCE_UNTIL_DATE_FORMAT.format(tQuery.getFrom());
        String until = SINCE_UNTIL_DATE_FORMAT.format(tQuery.getTo());

        Framework.debug("Since: " + since + ", Until: " + until);

        assert(since.equalsIgnoreCase(until));

        try
        {
            Framework.debug("1");
            twitter4j.Query externalQuery = new twitter4j.Query(searchTerm);
            Framework.debug("2");
//            externalQuery.setResultType(twitter4j.Query.ResultType.mixed); // mixed = popular & new
//            Framework.debug("3");
//            externalQuery.setSince(since);
//            Framework.debug("4");
//            externalQuery.setUntil(until);

            Framework.debug("5");
            QueryResult res = twitter.search(externalQuery);
            Framework.debug("6");
            return res.getTweets()
                            .stream()
                            .map(tweet -> {
                                Framework.debug("Mapping 1");
                                return new Document(tweet.getCreatedAt(), tweet.getText(), DocumentType.TWEET);} )
                            .collect(Collectors.toList());
        }
        catch (TwitterException e)
        {
            Framework.debug("7");
            e.printStackTrace();
            throw new FailedCollectionException(e);
        }
    }

    private Twitter newTwitterInstance()
    {
        // Get the bearer token
        OAuth2Token token = getOAuth2Token();

        //	Now, configure our new Twitter object to use application authentication and provide it with
        //	our CONSUMER key and secret and the bearer token we got back from Twitter
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setApplicationOnlyAuthEnabled(true);
        cb.setOAuthConsumerKey(consumerKey);
        cb.setOAuthConsumerSecret(consumerSecret);
        cb.setOAuth2TokenType(token.getTokenType());
        cb.setOAuth2AccessToken(token.getAccessToken());

        //	And create the Twitter object!
        Framework.info("Instantiating Twitter object using OAuth2Token");
        Twitter twitter = new TwitterFactory(cb.build()).getInstance();
        Framework.info("Successfully instantiated Twitter object using OAuth2Token");
        return twitter;
    }

    public OAuth2Token getOAuth2Token()
    {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setApplicationOnlyAuthEnabled(true);
        cb.setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret);

        try
        {
            Framework.info("Generating OAuth2Token");
            OAuth2Token token = new TwitterFactory(cb.build()).getInstance().getOAuth2Token();
            Framework.info("Successfully generated OAuth2Token");
            return token;
        }
        catch (Exception e)
        {
            Framework.error("Unable to generate OAuth Token", e);
            return null;
        }
    }

}
