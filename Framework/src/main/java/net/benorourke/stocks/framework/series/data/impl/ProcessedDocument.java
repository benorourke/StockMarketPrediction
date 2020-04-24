package net.benorourke.stocks.framework.series.data.impl;

import net.benorourke.stocks.framework.preprocess.FeatureRepresenter;
import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.DataType;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class ProcessedDocument extends Data
{
    private final Map<FeatureRepresenter<CleanedDocument>, double[]> featureVectors;

    public ProcessedDocument(Date date, Map<FeatureRepresenter<CleanedDocument>, double[]> featureVectors)
    {
        super(DataType.PROCESSED_DOCUMENT, date);

        this.featureVectors = featureVectors;
    }

    public Map<FeatureRepresenter<CleanedDocument>, double[]> getFeatureVectors()
    {
        return featureVectors;
    }

}
