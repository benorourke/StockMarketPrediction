package net.benorourke.stocks.framework.model.data;

public class ModelData
{
    /**
     * [0] = open
     * [1] = close
     * [2] = low
     * [3] = high
     * [4] = volume
     */
    private final double[] normalisedData;   // features
    private final double[] unnormalisedData; // labels

    public ModelData(double[] normalisedData, double[] unnormalisedData)
    {
        this.normalisedData = normalisedData;
        this.unnormalisedData = unnormalisedData;
    }

    public double[] getNormalisedData()
    {
        return normalisedData;
    }

    public double[] getUnnormalisedData()
    {
        return unnormalisedData;
    }

}
