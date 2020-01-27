package net.ben.stocks.framework.collection.api;

import net.ben.stocks.framework.collection.Constraint;
import net.ben.stocks.framework.collection.DataSource;
import net.ben.stocks.framework.collection.Query;
import net.ben.stocks.framework.collection.URLConnector;
import net.ben.stocks.framework.series.data.Document;
import net.ben.stocks.framework.exception.FailedCollectionException;

import java.io.*;
import java.util.*;

public class NewsAPI extends DataSource<Document>
{
    private static final String BASE_URL = "https://newsapi.org/";

    private final String apiKey;

    public NewsAPI(String apiKey)
    {
        this.apiKey = apiKey;
    }

    @Override
    public Class<? extends Document> getDataClass()
    {
        return Document.class;
    }

    @Override
    public Constraint[] getConstraints()
    {
        return new Constraint[0];
    }

    @Override
    public Collection<Document> retrieveNext(Query query) throws FailedCollectionException
    {
        try
        {
            String result = URLConnector.connect(BASE_URL.concat(buildUrlExtension(query))).read();
            System.out.println(result);
            return Arrays.asList(new Document(new Date(), result));
        }
        catch (IOException e)
        {
            throw new FailedCollectionException(e);
        }
    }

    ///v2/everything?q=apple&from=2019-12-20&to=2019-12-20&sortBy=popularity&apiKey=78d93a9d68584e61be38b1d90217d1e7
    private String buildUrlExtension(Query query)
    {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(query.getFrom());
        String strFrom = calendar.get(Calendar.YEAR)
                                + "-" + (calendar.get(Calendar.MONTH) + 1)
                                + "-" + calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTime(query.getTo());
        String strTo = calendar.get(Calendar.YEAR)
                                + "-" + (calendar.get(Calendar.MONTH) + 1)
                                + "-" + calendar.get(Calendar.DAY_OF_MONTH);

        return "v2/everything"
                    .concat("?q=" + query.getStock().getCompanyName())
                    .concat("&from=".concat(strFrom))
                    .concat("&to=".concat(strTo))
                    .concat("&sortBy=popularity")
                    .concat("&apiKey=" + apiKey);
//        return "v2/top-headlines?country=gb&apiKey=" + apiKey;
    }


}
