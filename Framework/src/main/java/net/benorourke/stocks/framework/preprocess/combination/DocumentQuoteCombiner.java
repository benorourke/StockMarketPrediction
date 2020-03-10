package net.benorourke.stocks.framework.preprocess.combination;

import net.benorourke.stocks.framework.model.ModelData;
import net.benorourke.stocks.framework.preprocess.Preprocess;
import net.benorourke.stocks.framework.series.data.impl.CleanedDocument;
import net.benorourke.stocks.framework.series.data.impl.ProcessedDocument;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;
import net.benorourke.stocks.framework.util.Tuple;

import java.util.Date;
import java.util.List;
import java.util.Map;

// TODO - Progress
public class DocumentQuoteCombiner extends Preprocess< Tuple<Map<Date, List<ProcessedDocument>>,
                                                             Map<Date, StockQuote>>,
                                                       List<ModelData>>
{

    @Override
    public void initialise() { }

    @Override
    public List<ModelData> preprocess(Tuple<Map<Date, List<ProcessedDocument>>,
                                            Map<Date, StockQuote>> data)
    {
        return null;
    }

}
