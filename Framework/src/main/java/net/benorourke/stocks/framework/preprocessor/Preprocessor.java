package net.benorourke.stocks.framework.preprocessor;

import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.ProcessedData;

import java.util.HashSet;
import java.util.Set;

public abstract class Preprocessor<S extends Data, U extends ProcessedData>
{
    private Set<Preprocess> preprocesses;

    public Preprocessor(Preprocess... preprocess)
    {
        preprocesses = new HashSet<Preprocess>();
        for (Preprocess elem : preprocess)
            preprocesses.add(elem);
    }

    public abstract String preprocess(String documentText);

    public boolean hasPreprocess(Preprocess preprocess)
    {
        return preprocesses.contains(preprocess);
    }

}
