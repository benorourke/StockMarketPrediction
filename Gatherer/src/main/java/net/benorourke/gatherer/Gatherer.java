package net.benorourke.gatherer;

import net.benorourke.gatherer.exception.FailedRetrievalException;
import net.benorourke.gatherer.newsapi.NewsAPI;

public class Gatherer
{

    public static void main(String[] args)
    {
        DataSource source = new NewsAPI("78d93a9d68584e61be38b1d90217d1e7");
        try
        {
            source.retrieve();
        }
        catch (FailedRetrievalException e)
        {
            e.printStackTrace();
        }
    }


}
