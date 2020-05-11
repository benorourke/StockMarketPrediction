package net.benorourke.stocks.framework.thread;

import net.benorourke.stocks.framework.util.Tuple;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A simple class to store the volatile progress of a task.
 */
public class Progress
{
    /**
     * Between 0 and 100.
     */
    private volatile double progress;

    public Progress()
    {
        this.progress = 0;
    }

    public double getProgress()
    {
        return progress;
    }

    public void setProgress(double progress)
    {
        this.progress = progress;
    }

    /**
     * A helper class that allows for sub-dividing tasks and automatically calculating percentage changed based on the
     * completion of a sub-division.
     */
    public static class Helper
    {
        private final Progress progress;
        private final Map<Integer, Tuple<Double, Double>> boundMappings;

        /**
         * Create a new instance.
         *
         * @param progress the progress object to help
         * @param weights the mappings of task sub-division IDs against their weight (summing 100)
         */
        public Helper(Progress progress, LinkedHashMap<Integer, Double> weights)
        {
            this.progress = progress;
            this.boundMappings = new LinkedHashMap<>();

            // from: stores the starting % of this subdivision
            // to: stores the ending % of this subdivision
            double from = 0;
            for (Map.Entry<Integer, Double> entry : weights.entrySet())
            {
                double to = from + entry.getValue();
                boundMappings.put(entry.getKey(), new Tuple<>(from, to));
                from = to;
            }
        }

        /**
         * Update the Progress percentage based on the completion of a sub-task.
         *
         * @param id the id of the sub-task
         * @param subPercentage the percentage completion of the sub-task 0-100
         * @return
         */
        public void updatePercentage(int id, double subPercentage)
        {
            Tuple<Double, Double> bounds = boundMappings.get(id);
            double from = bounds.getA(), to = bounds.getB();
            double delta = to - from;

            double truePercentage = from + (delta * Math.min(100, subPercentage) / 100);
            progress.progress = truePercentage;
        }

        public Tuple<Double, Double> getBounds(int id)
        {
            return boundMappings.get(id);
        }

    }

}
