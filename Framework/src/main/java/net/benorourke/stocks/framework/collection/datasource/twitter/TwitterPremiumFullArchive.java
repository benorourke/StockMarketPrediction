package net.benorourke.stocks.framework.collection.datasource.twitter;

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
