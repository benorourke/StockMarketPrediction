package net.benorourke.stocks.framework.thread.preprocessing;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.preprocess.Preprocess;
import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.thread.Progress;

public class PreprocessingProgress extends Progress
{
    private final Preprocess[] preprocesses;

    public PreprocessingProgress(Preprocess[] preprocesses)
    {
        this.preprocesses = preprocesses;
    }

    /**
     * When a stage has been completed; some have several (i.e. StockQuote & Document)
     * @param stage
     */
    public void onStageCompleted(PreprocessingStage stage)
    {
        // TODO
    }

    /**
     *
     * @param preprocessIndex
     * @param percentage the Data it's converting from
     */
    public void onPreprocessorPercentageChanged(int preprocessIndex, double percentage)
    {
        // TODO
        String strPreprocess = preprocesses[preprocessIndex].getClass().getSimpleName();
        Framework.debug("Received % change for preprocess " + strPreprocess + " (" + percentage + "%)");
    }

}
