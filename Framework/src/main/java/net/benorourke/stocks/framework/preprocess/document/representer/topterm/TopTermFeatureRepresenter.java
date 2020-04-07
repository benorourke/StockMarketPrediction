package net.benorourke.stocks.framework.preprocess.document.representer.topterm;

import net.benorourke.stocks.framework.preprocess.FeatureRepresenter;
import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.impl.CleanedDocument;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TopTermFeatureRepresenter implements FeatureRepresenter<CleanedDocument>
{
    private final RelevancyMetric relevancyMetric;
    private final int maxTopTerms;

    private String[] topTerms;

    public TopTermFeatureRepresenter(RelevancyMetric relevancyMetric, int maxTopTerms)
    {
        this.relevancyMetric = relevancyMetric;
        this.maxTopTerms = maxTopTerms;
    }

    @Override
    public DataType<CleanedDocument> getTypeFor()
    {
        return DataType.CLEANED_DOCUMENT;
    }

    @Override
    public void initialise(List<CleanedDocument> corpus)
    {
        relevancyMetric.initialise(corpus);
        topTerms = relevancyMetric.getMostRelevant(maxTopTerms);
    }

    @Override
    public int getVectorSize()
    {
        return topTerms.length;
    }

    @Override
    public double[] getVectorRepresentation(CleanedDocument document)
    {
        double[] vector = new double[topTerms.length];
        Arrays.fill(vector, 0.0);

        outer: for (int i = 0; i < topTerms.length; i ++)
        {
            String topTerm = topTerms[i];

            for (String token : document.getCleanedTerms())
            {
                if (topTerm.equals(token))
                {
                    vector[i] = 1.0;
                    continue outer;
                }
            }
        }

        return vector;
    }

    @Override
    public String getName()
    {
        return "Top Term";
    }

    @Override
    public CombinationPolicy getCombinationPolicy()
    {
        return CombinationPolicy.TAKE_HIGHEST;
    }

    public RelevancyMetric getRelevancyMetric()
    {
        return relevancyMetric;
    }

    public int getMaxTopTerms()
    {
        return maxTopTerms;
    }

    public String[] getTopTerms()
    {
        return topTerms;
    }

    public void setTopTerms(String[] topTerms)
    {
        this.topTerms = topTerms;
    }

}
