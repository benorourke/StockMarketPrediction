package net.benorourke.stocks.framework.preprocess.assignment;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.model.ModelData;
import net.benorourke.stocks.framework.preprocess.Preprocess;
import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.DatasetHelper;
import net.benorourke.stocks.framework.series.data.impl.ProcessedDocument;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;
import net.benorourke.stocks.framework.util.DateUtil;
import net.benorourke.stocks.framework.util.Tuple;

import java.util.*;
import java.util.stream.Collectors;

// TODO - Progress
public class LabelAssignment extends Preprocess< Tuple<List<ProcessedDocument>,
                                                       List<StockQuote>>,
                                                 List<ModelData>>
{
    private static final int PROGRESS_ITERATIONS = 10;

    private final MissingDataPolicy missingDataPolicy;
    private final ModelDataMapper mapper;

    public LabelAssignment(MissingDataPolicy missingDataPolicy, ModelDataMapper mapper)
    {
        this.missingDataPolicy = missingDataPolicy;
        this.mapper = mapper;
    }

    @Override
    public void initialise() {}

    @Override
    public List<ModelData> preprocess(Tuple<List<ProcessedDocument>, List<StockQuote>> data)
    {
        Framework.info("[Label Assignment] Combining Documents");
        Map<Date, List<Data>> combined = DatasetHelper.combine(data);
        Framework.info("[Label Assignment] " + combined.size() + " Days Found");

        for (Map.Entry<Date, List<Data>> entry : combined.entrySet())
        {
            Framework.debug("COMBINED DAY " + DateUtil.formatSimple(entry.getKey()));

            for (Data elem : entry.getValue())
            {
                Framework.debug("   " + elem.getClass().toString());
            }
        }

        Framework.info("[Label Assignment] Combining Saturday/Sunday Documents with Monday");
        combined = DatasetHelper.handleWeekends(combined);
        Framework.info("[Label Assignment] Combined Weekends. New Day Count: " + combined.size());

        Framework.info("[Label Assignment] Adding Missing Weekdays");
        fillEmptyWeekdays(combined);
        Framework.info("[Label Assignment] Added Missing Weekdays");

        Framework.info("[Label Assignment] Checking for Missing Datapoints");
        DataType[] checkFor = new DataType[] {DataType.PROCESSED_DOCUMENT, DataType.STOCK_QUOTE};
        Map<Date, List<DataType>> missingTypes = DatasetHelper.checkMissingDataTypes(combined, checkFor);
        Framework.info("[Label Assignment] Checked for Missing Datapoints (" + missingTypes.size() + " days)");
        for (Map.Entry<Date, List<DataType>> entry : missingTypes.entrySet())
        {
            Framework.info(DateUtil.formatSimple(entry.getKey()) + ": "
                                + entry.getValue().stream()
                                            .map(t -> String.valueOf(t.getName()))
                                            .collect(Collectors.joining(",")));
        }

        if (!missingTypes.isEmpty())
            missingDataPolicy.handle(combined, missingTypes);

        // Progress only changes here - the prior tasks are trivial
        return assignLabels(combined);
    }

    private List<ModelData> assignLabels(Map<Date, List<Data>> data)
    {
        int total = data.entrySet().stream().mapToInt(e -> e.getValue().size()).sum();
        int count = 0;

        List<ModelData> assigned = new ArrayList<>();
        for (Map.Entry<Date, List<Data>> entry : data.entrySet())
        {
            List<ProcessedDocument> docs = new ArrayList<>();
            // There should only be 1 StockQuote but we'll treat it as a list
            List<StockQuote> quotes = new ArrayList<>();

            for (Data elem : entry.getValue())
            {
                if (elem.getType().equals(DataType.PROCESSED_DOCUMENT))
                    docs.add((ProcessedDocument) elem);
                else if(elem.getType().equals(DataType.STOCK_QUOTE))
                    quotes.add((StockQuote) elem);
                else
                    Framework.info("Unable to cast " + elem.getClass().getSimpleName() + " while assigning labels");

                count ++;
                if(count % PROGRESS_ITERATIONS == 0)
                    onProgressChanged(( (double) count / (double) total) * 100 );
            }
            assigned.add(mapper.toModelData(entry.getKey(), docs, quotes));
        }

        onProgressChanged(100.0D);
        return assigned;
    }

    /**
     *
     * @param datesIter
     * @return [0] = earliest, [1] = latest
     */
    private Date[] getEarliestLatestDate(Iterable<Date> datesIter)
    {
        Date[] dates = new Date[2];
        for (Date date : datesIter)
        {
            if (dates[0] == null)
            {
                dates[0] = dates[1] = date;
                continue;
            }

            if (dates[0].after(date))
                dates[0] = date;
            if (dates[1].before(date))
                dates[1] = date;
        }
        return dates;
    }

    public void fillEmptyWeekdays(Map<Date, List<Data>> data)
    {
        Date[] dateRange = getEarliestLatestDate(data.keySet());
        Framework.info("[Label Assignment] Date Range:");
        Framework.info("[Label Assignment]    Earliest: " + DateUtil.formatSimple(dateRange[0]));
        Framework.info("[Label Assignment]    Latest: " + DateUtil.formatSimple(dateRange[1]));

        Date current = dateRange[0];
        while (current.before(dateRange[1]))
        {
            Date standard = DateUtil.getDayStart(current);

            if(!DateUtil.isWeekend(standard) && !data.containsKey(standard))
            {
                data.put(standard, new ArrayList<>());
                Framework.debug("[Label Assignment] Found Missing Week Day: " + DateUtil.formatSimple(standard));
            }

            current = DateUtil.addHours(current, 24);
        }
    }

    public ModelDataMapper getMapper()
    {
        return mapper;
    }

}
