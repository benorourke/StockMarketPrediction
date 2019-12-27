package net.ben.stocks;

import net.ben.stocks.framework.Framework;
import net.ben.stocks.framework.collection.DataSource;
import net.ben.stocks.framework.series.data.Document;
import net.ben.stocks.framework.stock.Stock;

public class Main
{

    public static void main(String[] args)
    {
        Framework framework = new Framework();
        framework.initialise();

        Stock stock = framework.getStockExchangeManager().getExchanges().get(0).getStocks().get(0);
        DataSource<Document> dataSource = framework.getDataSourceManager().getDataSourcesByClass(Document.class).get(0);

        //        Calendar cal = Calendar.getInstance();
        //        cal.setTime(new Date());
        //        cal.add(Calendar.DAY_OF_MONTH, -30);
        //        Date from = cal.getTime();
        //
        //        Date to = new Date();
        //        System.out.println("To: " + to.toString());
        //        System.out.println("From: " + from.toString());
        //
        //        Query query = new Query(to, from, stock);
        //
        //        try
        //        {
        //            dataSource.retrieveNext(query);
        //        }
        //        catch (FailedCollectionException e)
        //        {
        //            e.printStackTrace();
        //        }
    }

}
