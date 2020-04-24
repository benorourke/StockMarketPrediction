package net.benorourke.stocks.framework.persistence.gson.model;

import com.google.gson.*;
import net.benorourke.stocks.framework.model.ModelEvaluation;
import net.benorourke.stocks.framework.persistence.gson.JsonAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ModelEvaluationAdapter extends JsonAdapter<ModelEvaluation>
{

    @Override
    public JsonElement serialize(ModelEvaluation eval, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject result = new JsonObject();
        result.add("score", new JsonPrimitive((eval.getScore())));
        result.add("trainingPredictions", serializePredictions(eval.getTrainingPredictions()));
        result.add("testingPredictions", serializePredictions(eval.getTestingPredictions()));
        return result;
    }

    @Override
    public ModelEvaluation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        JsonObject object = json.getAsJsonObject();
        double score = object.getAsJsonPrimitive("score").getAsDouble();
        List<ModelEvaluation.Prediction> trainingPredictions = deserializePredictions(object.getAsJsonArray("trainingPredictions"));
        List<ModelEvaluation.Prediction> testingPredictions = deserializePredictions(object.getAsJsonArray("testingPredictions"));

        return new ModelEvaluation(score, trainingPredictions, testingPredictions);
    }

    private JsonArray serializePredictions(List<ModelEvaluation.Prediction> predictions)
    {
        JsonArray array = new JsonArray();

        for (ModelEvaluation.Prediction prediction : predictions)
        {
            JsonObject elem = new JsonObject();
            elem.add("date", new JsonPrimitive(prediction.getDate().getTime()));
            elem.add("labels", serializeDoubleArray(prediction.getLabels()));
            elem.add("predicted", serializeDoubleArray(prediction.getPredicted()));
            array.add(elem);
        }

        return array;
    }

    public List<ModelEvaluation.Prediction> deserializePredictions(JsonArray array)
    {
        List<ModelEvaluation.Prediction> predictions = new ArrayList<>();

        for (JsonElement elem : array)
        {
            JsonObject object = elem.getAsJsonObject();
            Date date = new Date(object.getAsJsonPrimitive("date").getAsLong());
            double[] labels = deserializeDoubleArray(object.getAsJsonArray("labels"));
            double[] predicted = deserializeDoubleArray(object.getAsJsonArray("predicted"));
            predictions.add(new ModelEvaluation.Prediction(date, labels, predicted));
        }

        return predictions;
    }

}
