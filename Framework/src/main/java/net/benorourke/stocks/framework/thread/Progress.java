package net.benorourke.stocks.framework.thread;

import net.benorourke.stocks.framework.util.Tuple;

import java.util.LinkedHashMap;
import java.util.Map;

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

    public static class Helper
    {
        private final Progress progress;
        private final Map<Integer, Tuple<Double, Double>> boundMappings;

        /**
         *
         * he sum of values within weights should equal 100
         *
         * @param progress
         * @param weights t
         */
        public Helper(Progress progress, LinkedHashMap<Integer, Double> weights)
        {
            this.progress = progress;
            this.boundMappings = new LinkedHashMap<>();

            double from = 0;
            for (Map.Entry<Integer, Double> entry : weights.entrySet())
            {
                double to = from + entry.getValue();
                boundMappings.put(entry.getKey(), new Tuple<>(from, to));
                from = to;
            }
        }

        /**
         *
         * @param id
         * @param subPercentage 0-100
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
