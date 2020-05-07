package net.benorourke.stocks.framework.collection.datasource.twitter;

public class TwitterFree extends TwitterEndpoint
{
    private static final String BASE_URL = "https://api.twitter.com/1.1/search/tweets.json";

    public TwitterFree()
    {
        super(EndpointType.FREE_7_DAYS);
    }

    public String getBaseUrl()
    {
        return BASE_URL;
    }

}
