package net.ben.stocks;

import net.ben.stocks.framework.Configuration;
import net.ben.stocks.framework.Framework;
import net.ben.stocks.framework.collection.DataSource;
import net.ben.stocks.framework.collection.Query;
import net.ben.stocks.framework.exception.ConstraintException;
import net.ben.stocks.framework.exception.FailedCollectionException;
import net.ben.stocks.framework.series.data.Document;
import net.ben.stocks.framework.stock.Stock;
import net.ben.stocks.framework.thread.ResultCallback;
import net.ben.stocks.framework.thread.impl.collection.CollectionResult;
import net.ben.stocks.framework.thread.impl.collection.CollectionTask;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Main
{

    public static void main(String[] args)
    {
        testTaskManager();
    }

    public static void testTaskManager()
    {
        Configuration config = new Configuration();
        config.setWorkingDirectory(new File("D:\\Storage\\Desktop\\Stocks"));
        Framework framework = new Framework(config);
        framework.initialise();

        CollectionTask taskA = new CollectionTask("Task A");
        CollectionTask taskB = new CollectionTask("Task B");
        framework.getTaskManager().scheduleRepeating(taskA, new ResultCallback<CollectionResult>()
        {
            public void onCallback(CollectionResult result)
            {

            }
        }, 200, 500, TimeUnit.MILLISECONDS);
        framework.getTaskManager().scheduleRepeating(taskB, new ResultCallback<CollectionResult>()
        {
            public void onCallback(CollectionResult result)
            {

            }
        }, 200, 500, TimeUnit.MILLISECONDS);
    }

    public static void testTimeSeries()
    {
        Configuration config = new Configuration();
        config.setWorkingDirectory(new File("D:\\Storage\\Desktop\\Stocks"));
        Framework framework = new Framework(config);
        framework.initialise();

        Stock stock = framework.getStockExchangeManager().getExchanges().get(0).getStocks().get(0);
        DataSource<Document> dataSource = framework.getDataSourceManager().getDataSourcesByClass(Document.class).get(0);

        framework.getTimeSeriesManager().create(UUID.randomUUID().toString(), stock);
    }

    public static void testCollection()
    {
        Configuration config = new Configuration();
        config.setWorkingDirectory(new File("D:\\Storage\\Desktop\\Stocks"));
        Framework framework = new Framework(config);
        framework.initialise();

        Stock stock = framework.getStockExchangeManager().getExchanges().get(0).getStocks().get(0);
        DataSource<Document> dataSource = framework.getDataSourceManager().getDataSourcesByClass(Document.class).get(0);

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, -27);
        Date from = cal.getTime();

        Date to = new Date();
        System.out.println("To: " + to.toString());
        System.out.println("From: " + from.toString());

        Query query = new Query(to, from, stock);

        try
        {
            dataSource.retrieve(query);
        }
        catch (FailedCollectionException e)
        {
            e.printStackTrace();
        }
        catch (ConstraintException e)
        {
            e.printStackTrace();
        }
    }

}
