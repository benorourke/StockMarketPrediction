package net.benorourke.stocks.framework.persistence.gson.model;

import com.google.gson.*;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.model.ModelData;
import net.benorourke.stocks.framework.persistence.gson.JsonAdapter;

import java.lang.reflect.Type;
import java.util.Date;

public class ModelDataAdapter extends JsonAdapter<ModelData>
{

    @Override
    public JsonElement serialize(ModelData data, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject result = new JsonObject();
        result.add("date", new JsonPrimitive(data.getDate().getTime()));
        result.add("features", serializeDoubleArray(data.getFeatures()));
        result.add("labels", serializeDoubleArray(data.getLabels()));
        return result;
    }

    @Override
    public ModelData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        JsonObject object = json.getAsJsonObject();
        Date date = new Date(object.getAsJsonPrimitive("date").getAsLong());
        double[] features = deserializeDoubleArray(object.getAsJsonArray("features"));
        double[] labels = deserializeDoubleArray(object.getAsJsonArray("labels"));
        return new ModelData(date, features, labels);
    }

}
