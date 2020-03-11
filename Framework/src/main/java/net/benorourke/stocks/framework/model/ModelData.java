package net.benorourke.stocks.framework.model;

import java.util.Date;

public class ModelData
{
    public static int N_FEATURES = 5, N_LABELS = 5;

    /**
     * As of now:
     * [0] = open
     * [1] = close
     * [2] = low
     * [3] = high
     * [4] = volume
     *
     * TODO
     */
    private final Date date;
    private final double[] features;   // normalised feedforward
    private final double[] labels; // unnormalised feedforward

    public ModelData(Date date, double[] features, double[] labels)
    {
        this.date = date;
        this.features = features;
        this.labels = labels;
    }

    public Date getDate()
    {
        return date;
    }

    public double[] getFeatures()
    {
        return features;
    }

    public double[] getLabels()
    {
        return labels;
    }

}
