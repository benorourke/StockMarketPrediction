package net.benorourke.stocks.framework.collection.session;

import net.benorourke.stocks.framework.collection.Query;
import net.benorourke.stocks.framework.collection.session.filter.CollectionFilter;
import net.benorourke.stocks.framework.series.data.Data;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Splits the query up into compounded daily queries.
 */
public class DailyCollectionSession<T extends Data> extends CollectionSession<T>
{
    private final Query completeQuery;
    private Date currentDate;

    private int completed;

    public DailyCollectionSession(Query completeQuery, CollectionFilter<T> collectionFilter)
    {
        super(collectionFilter);

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

        return new Query(prev, prev);
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
