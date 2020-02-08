package net.ben.stocks.framework.collection.session;

import net.ben.stocks.framework.collection.Query;
import net.ben.stocks.framework.exception.ConstraintException;
import net.ben.stocks.framework.exception.FailedCollectionException;
import net.ben.stocks.framework.series.data.Data;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DailyCollectionSession<T extends Data> implements CollectionSession<T>
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

        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        currentDate = cal.getTime();

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
        return (int) ((completeQuery.getTo().getTime() - currentDate.getTime()) / TimeUnit.DAYS.toMillis(1));
    }

}