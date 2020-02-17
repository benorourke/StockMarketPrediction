package net.benorourke.stocks.framework.preprocessor;

import java.util.HashSet;
import java.util.Set;

public abstract class Preprocessor
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
