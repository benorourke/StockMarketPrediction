package net.benorourke.stocks.framework.preprocess;

import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.impl.CleanedDocument;

import java.util.List;
import java.util.Map;

/**
 * To work, each new FeatureRepresenter needs a respective GSON Adapter injected into the Configuration.
 *
 * @param <T>
 */
public interface FeatureRepresenter<T extends Data>
{
    enum CombinationPolicy
    {
        TAKE_HIGHEST,
        TAKE_MEAN_AVERAGE,
        TAKE_MODE_AVERAGE
    }

    DataType<T> getTypeFor();

    /**
     * Called before each datapoint is individually represented.
     *
     * @param allData
     */
    void initialise(List<T> allData);

    int getVectorSize();

    double[] getVectorRepresentation(T datapoint);

    String getName();

    /**
     * The policy to handle combining features for documents / quotes on the same day.
     * @return
     */
    CombinationPolicy getCombinationPolicy();

}
