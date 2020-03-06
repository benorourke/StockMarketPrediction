package net.benorourke.stocks.framework.persistence.gson.model;

import com.google.gson.*;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.model.ModelData;
import net.benorourke.stocks.framework.persistence.gson.JsonAdapter;

import java.lang.reflect.Type;
import java.util.Date;

public class ModelDataAdapter implements JsonAdapter<ModelData>
{

    @Override
    public JsonElement serialize(ModelData data, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject result = new JsonObject();
        result.add("date", new JsonPrimitive(data.getDate().getTime()));

        JsonArray features = new JsonArray();
        for (double feature : data.getFeatures())
            features.add(feature);
        result.add("features", features);

        JsonArray labels = new JsonArray();
        for (double label : data.getLabels())
            labels.add(label);
        result.add("labels", labels);

        return result;
    }

    @Override
    public ModelData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        JsonObject object = json.getAsJsonObject();
        Date date = new Date(object.getAsJsonPrimitive("date").getAsLong());

        Framework.debug("Loading features & labels");

        double[] features = readDoubleArray(object.getAsJsonArray("features"));
        double[] labels = readDoubleArray(object.getAsJsonArray("labels"));

        Framework.debug("Loaded features & labels");

        return new ModelData(date, features, labels);
    }

    private double[] readDoubleArray(JsonArray jsonArray)
    {
        double[] array = new double[jsonArray.size()];
        for (int i = 0; i < array.length; i ++)
        {
            array[i] = jsonArray.get(i).getAsDouble();
        }
        return array;
    }

}
