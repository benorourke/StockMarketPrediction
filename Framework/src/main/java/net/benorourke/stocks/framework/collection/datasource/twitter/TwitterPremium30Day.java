package net.benorourke.stocks.framework.collection.datasource.twitter;

public class TwitterPremium30Day extends PremiumTwitterEndpoint
{
    private static final String BASE_URL = "https://api.twitter.com/1.1/search/30day/";

    public TwitterPremium30Day()
    {
        super(EndpointType.PREMIUM_30_DAYS);
    }

    @Override
    public String getBaseUrl()
    {
        return BASE_URL.concat(getEnvironment()).concat(".json");
    }

}
