package net.benorourke.stocks.framework.preprocessor;

import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.ProcessedData;

import java.util.HashSet;
import java.util.Set;

public interface Preprocessor<S extends Data, U extends ProcessedData>
{

    void initialise();

    U preprocess(S data);

}
