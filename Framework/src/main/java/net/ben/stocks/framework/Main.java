package net.ben.stocks.framework;

import net.ben.stocks.framework.collection.DataSource;
import net.ben.stocks.framework.collection.NewsAPI;
import net.ben.stocks.framework.exception.FailedCollectionException;

public class Main
{

    public static void main(String[] args)
    {
        DataSource source = new NewsAPI("78d93a9d68584e61be38b1d90217d1e7");
        try
        {
            source.retrieveNext();
        }
        catch (FailedCollectionException e)
        {
            e.printStackTrace();
        }
    }

}
