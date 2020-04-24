package net.benorourke.stocks.framework.model;

import java.util.Date;
import java.util.List;

public class ModelEvaluation
{
    private double score;
    private final List<Prediction> trainingPredictions;
    private final List<Prediction> testingPredictions;

    public ModelEvaluation(double score, List<Prediction> trainingPredictions, List<Prediction> testingPredictions)
    {
        this.score = score;
        this.trainingPredictions = trainingPredictions;
        this.testingPredictions = testingPredictions;
    }

    public double getScore()
    {
        return score;
    }

    public List<Prediction> getTrainingPredictions()
    {
        return trainingPredictions;
    }

    public List<Prediction> getTestingPredictions()
    {
        return testingPredictions;
    }

    public static class Prediction
    {
        private Date date;
        private double[] labels;
        private double[] predicted;

        public Prediction(Date date, double[] labels, double[] predicted)
        {
            this.date = date;
            this.labels = labels;
            this.predicted = predicted;
        }

        public Date getDate()
        {
            return date;
        }

        public double[] getLabels()
        {
            return labels;
        }

        public double[] getPredicted()
        {
            return predicted;
        }
    }

}
