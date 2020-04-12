package net.benorourke.stocks.framework.model;

import java.util.Date;

public class ModelData
{
    private final Date date;
    private final double[] features;
    private final double[] labels;

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
