package net.benorourke.stocks.framework.preprocess.document;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.preprocess.Preprocess;
import net.benorourke.stocks.framework.preprocess.FeatureRepresentor;
import net.benorourke.stocks.framework.series.data.impl.CleanedDocument;
import net.benorourke.stocks.framework.series.data.impl.ProcessedDocument;

import java.util.*;

public class FeatureRepresentation extends Preprocess<List<CleanedDocument>, List<ProcessedDocument>>
{
    private static final int PROGRESS_ITERATIONS = 10;

    private final List<FeatureRepresentor<CleanedDocument>> representors;

    public FeatureRepresentation(List<FeatureRepresentor<CleanedDocument>> representors)
    {
        this.representors = representors;
    }

    @Override
    public void initialise() { }

    @Override
    public List<ProcessedDocument> preprocess(List<CleanedDocument> data)
    {
        // Progress 0% - 50%
        Framework.info("[FeatureRepresentation (1/2)] Initialising FeatureRepresentors");
        initRepresentors(data);

        // Progress 50% - 100%
        Framework.info("[FeatureRepresentation (2/2)] Beginning CleanedDocument -> ProcesesdDocument using features");
        return representFeatures(data);
    }

    public List<FeatureRepresentor<CleanedDocument>> getRepresentors()
    {
        return representors;
    }

    private void initRepresentors(List<CleanedDocument> data)
    {
        int total = representors.size();
        int count = 0;

        for (FeatureRepresentor<CleanedDocument> representor : representors)
        {
            Framework.info("[FeatureRepresentation (1/2)] Initialising FeatureRepresentor " + representor.getName());
            representor.initialise(data);
            Framework.info("[FeatureRepresentation (1/2)] Initialised FeatureRepresentor " + representor.getName());

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
            Map<FeatureRepresentor<CleanedDocument>, double[]> vectors = new HashMap<>();
            for (FeatureRepresentor<CleanedDocument> representor : representors)
            {
                vectors.put(representor, representor.getVectorRepresentation(document));
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
