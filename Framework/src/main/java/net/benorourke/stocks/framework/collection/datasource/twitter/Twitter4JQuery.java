package net.benorourke.stocks.framework.collection.datasource.twitter;

import net.benorourke.stocks.framework.collection.Query;
import twitter4j.Twitter;

import java.util.Date;

public class Twitter4JQuery extends Query
{
    private Twitter twitter;

    public Twitter4JQuery(Twitter twitter, Date to, Date from)
    {
        super(to, from);
    }

    public Twitter getTwitter()
    {
        return twitter;
    }
}
