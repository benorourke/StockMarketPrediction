package net.benorourke.stocks.framework.preprocess;

import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.ProcessedData;
import net.benorourke.stocks.framework.util.Initialisable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Preprocess<S, U> implements Initialisable
{
    private final Set<ProgressCallback> progressCallbacks;

    public Preprocess()
    {
        progressCallbacks = new HashSet<ProgressCallback>();
    }

    public abstract U preprocess(S data);

    public void addProgressCallback(ProgressCallback callback)
    {
        progressCallbacks.add(callback);
    }

    public void onProgressChanged(double progress)
    {
        progressCallbacks.stream().forEach(c -> c.onProgressUpdate(progress));
    }

    public void removeProgressCallback(ProgressCallback callback)
    {
        progressCallbacks.remove(callback);
    }

}
