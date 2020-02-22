package net.benorourke.stocks.framework.preprocessor;

import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.ProcessedData;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface Preprocessor<S extends Data, U extends ProcessedData>
{

    void initialise();

    List<U> preprocess(List<S> data);

}
