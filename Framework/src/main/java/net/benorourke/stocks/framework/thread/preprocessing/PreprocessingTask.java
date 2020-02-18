package net.benorourke.stocks.framework.thread.preprocessing;

import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.impl.Document;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;
import net.benorourke.stocks.framework.thread.Progress;
import net.benorourke.stocks.framework.thread.Task;
import net.benorourke.stocks.framework.thread.TaskDescription;
import net.benorourke.stocks.framework.thread.TaskType;

import java.util.*;

/**
 * No TaskDescription derivative class required as we only want to enable one
 * pre-processing task at a time.
 */
public class PreprocessingTask implements Task<TaskDescription, PreprocessingResult>
{
    public enum Stage { LOADING_QUOTES, DONE; }

    private Stage stage;
    private DataSource<StockQuote> stockQuoteSource;
    private List<DataSource<Document>> documentSources;

    private Progress progress;

    public PreprocessingTask(Map<DataSource, Integer> collectedDataCounts)
    {
        stage = Stage.LOADING_QUOTES;

        Map<DataType, List<DataSource>> grouped = group(collectedDataCounts.keySet());
        stockQuoteSource = (DataSource<StockQuote>) grouped.get(DataType.STOCK_QUOTE).get(0);
        documentSources = new ArrayList<>();
        for (DataSource source : grouped.get(DataType.DOCUMENT))
            documentSources.add( (DataSource<Document>) source);
    }

    @Override
    public TaskType getType()
    {
        return TaskType.PRE_PROCESSING;
    }

    @Override
    public TaskDescription getDescription()
    {
        return new TaskDescription(TaskType.PRE_PROCESSING)
        {
            @Override
            public boolean equals(Object object)
            {
                return object instanceof TaskDescription
                        && ((TaskDescription) object).getType().equals(getType());
            }
        };
    }

    @Override
    public Progress createTaskProgress()
    {
        return progress = new Progress();
    }

    @Override
    public void run()
    {
    }

    @Override
    public boolean isFinished()
    {
        return stage.equals(Stage.DONE);
    }

    @Override
    public PreprocessingResult getResult()
    {
        // TODO
        return new PreprocessingResult();
    }

    private Map<DataType, List<DataSource>> group(Collection<DataSource> sources)
    {
        Map<DataType, List<DataSource>> grouped = new HashMap<>();

        for (DataSource source : sources)
        {
            DataType dataType = source.getDataType();

            if(!grouped.containsKey(dataType))
                grouped.put(dataType, new ArrayList<>());

            grouped.get(dataType).add(source);
        }

        return grouped;
    }

}
