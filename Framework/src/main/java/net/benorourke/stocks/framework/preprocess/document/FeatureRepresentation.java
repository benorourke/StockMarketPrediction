package net.benorourke.stocks.framework.preprocess.document;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.preprocess.Preprocess;
import net.benorourke.stocks.framework.preprocess.FeatureRepresenter;
import net.benorourke.stocks.framework.series.data.impl.CleanedDocument;
import net.benorourke.stocks.framework.series.data.impl.ProcessedDocument;

import java.util.*;

// TODO - Progress
public class FeatureRepresentation extends Preprocess<List<CleanedDocument>, List<ProcessedDocument>>
{
    private final List<FeatureRepresenter<CleanedDocument>> representers;

    public FeatureRepresentation(List<FeatureRepresenter<CleanedDocument>> representers)
    {
        this.representers = representers;
    }

    @Override
    public void initialise() { }

    @Override
    public List<ProcessedDocument> preprocess(List<CleanedDocument> data)
    {
        for (FeatureRepresenter<CleanedDocument> representer : representers)
        {
            Framework.info("[FeatureRepresentation (1/2)] Initialising FeatureRepresenter " + representer.getName());
            representer.initialise(data);
            Framework.info("[FeatureRepresentation (1/2)] Initialised FeatureRepresenter " + representer.getName());
        }

        Framework.info("[FeatureRepresentation (2/2)] Beginning CleanedDocument -> ProcesesdDocument using features");
        List<ProcessedDocument> processed = new ArrayList<>();
        for (CleanedDocument document : data)
        {
            Map<FeatureRepresenter<CleanedDocument>, double[]> vectors = new HashMap<>();
            for (FeatureRepresenter<CleanedDocument> representer : representers)
            {
                vectors.put(representer, representer.getVectorRepresentation(document));
            }
        }
        Framework.info("[FeatureRepresentation (2/2)] Finished CleanedDocument -> ProcesesdDocument using features");
        return processed;
    }

    public List<FeatureRepresenter<CleanedDocument>> getRepresenters()
    {
        return representers;
    }

}
