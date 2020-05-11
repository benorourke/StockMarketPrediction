package net.benorourke.stocks.framework.persistence.gson;

import com.google.gson.*;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.series.TimeSeries;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimeSeriesAdapter extends JsonAdapter<TimeSeries>
{

    @Override
    public JsonElement serialize(TimeSeries series, Type typeOfSrc, JsonSerializationContext context)
    {
        Framework.debug("Calling TimeSeries Serializer");
        JsonObject result = new JsonObject();
        result.add("id", context.serialize(series.getId().toString()));
        result.add("name", context.serialize(series.getName()));
        result.add("stock", new JsonPrimitive(series.getStock()));

        // Serialize the data count map
        JsonObject rawDataCounts = new JsonObject();
        for (Map.Entry<Class<? extends DataSource>, Integer> entry : series.getRawDataCounts().entrySet())
        {
            rawDataCounts.add(entry.getKey().getName(), new JsonPrimitive(entry.getValue()));
            Framework.debug("Injected " + entry.getKey().getName() + ": " + entry.getValue());
        }
        result.add("rawDataCounts", rawDataCounts);
        return result;
    }

    @Override
    public TimeSeries deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        JsonObject object = json.getAsJsonObject();
        UUID id = UUID.fromString(object.getAsJsonPrimitive("id").getAsString());
        String name = object.getAsJsonPrimitive("name").getAsString();
        String stock = object.getAsJsonPrimitive("stock").getAsString();

        // Deserialize the data count map
        Map<Class<? extends DataSource>, Integer> typedMap = new HashMap<>();
        JsonObject rawDataCounts = object.getAsJsonObject("rawDataCounts");
        for (String key : rawDataCounts.keySet())
        {
            try
            {
                // Get the class of this DataSource
                Class<?> srcClass = Class.forName(key);
                int count = rawDataCounts.getAsJsonPrimitive(key).getAsInt();
                typedMap.put((Class<? extends DataSource>) srcClass, count);
                Framework.debug("Deserialized " + key + ": " + count);
            }
            catch (ClassNotFoundException e)
            {
                Framework.error("Unable to deserialize DataSource Class " + key, e);
            }
        }
        return new TimeSeries(id, name, stock, typedMap);
    }


}
