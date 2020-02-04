package net.ben.stocks.framework.collection;

import net.ben.stocks.framework.collection.CollectionSession;
import net.ben.stocks.framework.collection.Query;
import net.ben.stocks.framework.util.Nullable;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DailyCollectionSession implements CollectionSession
{
    private final Query completeQuery;
    private Date currentDate;

    private int completed;

    public DailyCollectionSession(Query completeQuery)
    {
        this.completeQuery = completeQuery;
        currentDate = completeQuery.getFrom();
    }

    @Override
    public boolean isFinished()
    {
        return currentDate.equals(completeQuery.getTo()) || currentDate.after(completeQuery.getTo());
    }

    @Override
    public Query nextQuery()
    {
        completed ++;

        Date prev = new Date(currentDate.getTime());
        currentDate = new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1));

        return new Query(prev, prev, completeQuery.getStock());
    }

    @Override
    public int completed()
    {
        return completed;
    }

    @Override
    public int remaining()
    {
        int days = (int) ((completeQuery.getTo().getTime() - currentDate.getTime()) / TimeUnit.DAYS.toMillis(1));
        return 1 + days;
    }

}
