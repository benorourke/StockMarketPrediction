package net.benorourke.stocks.framework.preprocess;

import net.benorourke.stocks.framework.util.Initialisable;

import java.util.HashSet;
import java.util.Set;

public abstract class Preprocess<S, U> implements Initialisable
{
    private final Set<ProgressAdapter> progressAdapters;

    public Preprocess()
    {
        progressAdapters = new HashSet<ProgressAdapter>();
    }

    public abstract U preprocess(S data);

    public void addProgressAdapter(ProgressAdapter callback)
    {
        progressAdapters.add(callback);
    }

    public void onProgressChanged(double progress)
    {
        progressAdapters.stream().forEach(c -> c.onProgressUpdate(progress));
    }

    public void removeProgressAdapter(ProgressAdapter callback)
    {
        progressAdapters.remove(callback);
    }

}
