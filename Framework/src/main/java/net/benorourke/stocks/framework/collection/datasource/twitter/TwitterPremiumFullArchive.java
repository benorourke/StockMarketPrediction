package net.benorourke.stocks.framework.collection.datasource.twitter;

/**
 * The premium twitter endpoint where tweets from up all time can be accessed.
 */
public class TwitterPremiumFullArchive extends PremiumTwitterEndpoint
{
    private static final String BASE_URL = "https://api.twitter.com/1.1/search/fullarchive/";

    public TwitterPremiumFullArchive()
    {
        super(EndpointType.PREMIUM_ALL_TIME);
    }

    @Override
    public String getBaseUrl()
    {
        return BASE_URL.concat(getEnvironment()).concat(".json");
    }

}
