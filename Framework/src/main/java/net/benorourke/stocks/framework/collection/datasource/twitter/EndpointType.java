package net.benorourke.stocks.framework.collection.datasource.twitter;

public enum EndpointType
{
    FREE_7_DAYS("Free (~7 Days)"),
    PREMIUM_30_DAYS("Premium (~30 Days)"),
    PREMIUM_ALL_TIME("Premium (All Time)");

    private String locale;

    EndpointType(String locale)
    {
        this.locale = locale;
    }

    public String getLocale()
    {
        return locale;
    }
}
