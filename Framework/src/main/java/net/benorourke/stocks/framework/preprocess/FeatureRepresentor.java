package net.benorourke.stocks.framework.preprocess;

import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.DataType;

import java.util.List;

/**
 * To work, each new FeatureRepresentor needs a respective GSON Adapter injected into the Configuration.
 *
 * @param <T>
 */
public interface FeatureRepresentor<T extends Data>
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
     */
    void initialise(List<T> allData);

    int getVectorSize();

    double[] getVectorRepresentation(T datapoint);

    String getName();

    /**
     * The policy to handleUserInterfaceInputs combining features for documents / quotes on the same day.
     * @return
     */
    CombinationPolicy getCombinationPolicy();

}
