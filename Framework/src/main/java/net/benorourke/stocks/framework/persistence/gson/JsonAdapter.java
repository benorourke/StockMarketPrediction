package net.benorourke.stocks.framework.persistence.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

public abstract class JsonAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T>
{

    public JsonArray serializeDoubleArray(double[] array)
    {
        JsonArray jsonArray = new JsonArray();
        for (double elem : array)
            jsonArray.add(elem);
        return jsonArray;
    }

    public double[] deserializeDoubleArray(JsonArray jsonArray)
    {
        double[] array = new double[jsonArray.size()];
        for (int i = 0; i < array.length; i ++)
        {
            array[i] = jsonArray.get(i).getAsDouble();
        }
        return array;
    }

}
