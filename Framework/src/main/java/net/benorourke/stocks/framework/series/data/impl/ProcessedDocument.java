package net.benorourke.stocks.framework.series.data.impl;

import net.benorourke.stocks.framework.preprocess.FeatureRepresentor;
import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.DataType;

import java.util.Date;
import java.util.Map;

/**
 * A Document that has gone the pre-processing lifecycle:
 * Document to Cleaned Document to Processed Document
 *
 * Within it is stored all of the features to be used by a model.
 */
public class ProcessedDocument extends Data
{
    private final Map<FeatureRepresentor<CleanedDocument>, double[]> featureVectors;

    /**
     * Create a new instance.
     *
     * @param date the date of the document
     * @param featureVectors the mappings against feature representors and the vectors to combine as input
     */
    public ProcessedDocument(Date date, Map<FeatureRepresentor<CleanedDocument>, double[]> featureVectors)
    {
        super(DataType.PROCESSED_DOCUMENT, date);

        this.featureVectors = featureVectors;
    }

    /**
     * Duplicate data is a problem at the cleaning level, once here (during later pre-processing stages),
     * this becomes less relevant.
     *
     * @param other
     * @return
     */
    @Override
    public boolean isDuplicate(Data other)
    {
        return false;
    }

    public Map<FeatureRepresentor<CleanedDocument>, double[]> getFeatureVectors()
    {
        return featureVectors;
    }

}
