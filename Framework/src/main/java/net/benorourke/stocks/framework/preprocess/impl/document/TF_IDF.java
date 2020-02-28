package net.benorourke.stocks.framework.preprocess.impl.document;

import net.benorourke.stocks.framework.series.data.impl.CleanedDocument;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class TF_IDF
{
    private TF_IDF() {}

    public static TF_IDF generate(Map<Date, List<CleanedDocument>> data)
    {
        TF_IDF obj = new TF_IDF();

        // TODO

        return obj;
    }

}
