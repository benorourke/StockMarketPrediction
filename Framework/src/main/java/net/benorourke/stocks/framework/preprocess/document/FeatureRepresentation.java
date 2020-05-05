package net.benorourke.stocks.framework.preprocess.document;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.preprocess.Preprocess;
import net.benorourke.stocks.framework.preprocess.FeatureRepresenter;
import net.benorourke.stocks.framework.series.data.impl.CleanedDocument;
import net.benorourke.stocks.framework.series.data.impl.ProcessedDocument;

import java.util.*;

public class FeatureRepresentation extends Preprocess<List<CleanedDocument>, List<ProcessedDocument>>
{
    private static final int PROGRESS_ITERATIONS = 10;

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
        // Progress 0% - 50%
        Framework.info("[FeatureRepresentation (1/2)] Initialising FeatureRepresenters");
        initRepresenters(data);

        // Progress 50% - 100%
        Framework.info("[FeatureRepresentation (2/2)] Beginning CleanedDocument -> ProcesesdDocument using features");
        return representFeatures(data);
    }

    public List<FeatureRepresenter<CleanedDocument>> getRepresenters()
    {
        return representers;
    }

    private void initRepresenters(List<CleanedDocument> data)
    {
        int total = representers.size();
        int count = 0;

        for (FeatureRepresenter<CleanedDocument> representer : representers)
        {
            Framework.info("[FeatureRepresentation (1/2)] Initialising FeatureRepresenter " + representer.getName());
            representer.initialise(data);
            Framework.info("[FeatureRepresentation (1/2)] Initialised FeatureRepresenter " + representer.getName());

            onProgressChanged(( (double) count / (double) total) * 50 );
        }
    }

    private List<ProcessedDocument> representFeatures(List<CleanedDocument> data)
    {
        int total = data.size();
        int count = 0;

        List<ProcessedDocument> processed = new ArrayList<>();
        for (CleanedDocument document : data)
        {
            Map<FeatureRepresenter<CleanedDocument>, double[]> vectors = new HashMap<>();
            for (FeatureRepresenter<CleanedDocument> representer : representers)
            {
                vectors.put(representer, representer.getVectorRepresentation(document));
            }
            processed.add(new ProcessedDocument(document.getDate(), vectors));

            count ++;
            if(count % PROGRESS_ITERATIONS == 0)
                onProgressChanged(50.0 + ((double) count / (double) total) * 50 );
        }
        Framework.info("[FeatureRepresentation (2/2)] Finished CleanedDocument -> ProcesesdDocument using features");
        return processed;
    }


}
