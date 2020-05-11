package net.benorourke.stocks.framework.model;

import java.util.Date;
import java.util.List;

/**
 * An storage struct to be produced when evaluating a model.
 *
 * This allows for visual representations of a model's predictions
 */
public class ModelEvaluation
{
    private double score;
    /**
     * Predictions to be made based on training data may also show insight into the model.if there is not enough
     * data.
     */
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

    /**
     * A struct for actual labels mapped against the predicted labels on a given date.
     */
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
