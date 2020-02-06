package net.ben.stocks.framework.collection.api;

import net.ben.stocks.framework.Framework;
import net.ben.stocks.framework.collection.*;
import net.ben.stocks.framework.collection.constraint.Constraint;
import net.ben.stocks.framework.collection.constraint.MaximumAgeConstraint;
import net.ben.stocks.framework.collection.constraint.OrderingConstraint;
import net.ben.stocks.framework.collection.session.CollectionSession;
import net.ben.stocks.framework.collection.session.DailyCollectionSession;
import net.ben.stocks.framework.collection.session.api.NewsAPICollectionSession;
import net.ben.stocks.framework.exception.ConstraintException;
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
        return new Constraint[]
                {
                        new OrderingConstraint(),
                        new MaximumAgeConstraint((int) 28)
                };
    }

    @Override
    public CollectionSession newSession(Query completeQuery)
    {
        return new NewsAPICollectionSession(completeQuery);
    }

    @Override
    public Collection<Document> retrieve(Query query) throws ConstraintException, FailedCollectionException
    {
        checkConstraints(query);

        try
        {
            String url = BASE_URL.concat(buildUrlExtension(query));
            Framework.info("Connecting to " + url);

            URLConnector connector = URLConnector.connect(url);
            String result = connector.read();

            if(connector.getResponseCode() != URLConnector.RESPONSE_OK)
            {
                throw new FailedCollectionException(connector.getResponseCode());
            }
            else
            {
                // TODO: Parse response and return
                System.out.println(result);
                return Arrays.asList(new Document(new Date(), result));
            }
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
                    .concat("?q=" + query.getStock().getCompanyName().replace(" ", "%20"))
                    .concat("&from=".concat(strFrom))
                    .concat("&to=".concat(strTo))
                    .concat("&sortBy=popularity")
                    .concat("&apiKey=" + apiKey);
//        return "v2/top-headlines?country=gb&apiKey=" + apiKey;
    }


}
