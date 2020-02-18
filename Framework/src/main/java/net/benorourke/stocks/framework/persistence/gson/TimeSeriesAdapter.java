package net.benorourke.stocks.framework.persistence.gson;

import com.google.gson.*;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.stock.Stock;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TimeSeriesAdapter implements JsonAdapter<TimeSeries>
{

    @Override
    public JsonElement serialize(TimeSeries series, Type typeOfSrc, JsonSerializationContext context)
    {
        Framework.debug("Calling TimeSeries Serializer");
        JsonObject result = new JsonObject();
        result.add("name", context.serialize(series.getName()));
        result.add("stock", context.serialize(series.getStock()));

        Map<String, Integer> dataCountMap = new HashMap<>();
        for (Map.Entry<Class<? extends DataSource>, Integer> entry : series.getRawDataCounts().entrySet())
        {
            dataCountMap.put(entry.getKey().getName(), entry.getValue());
        }
        result.add("rawDataCounts", context.serialize(dataCountMap));
        return result;
    }

    @Override
    public TimeSeries deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        JsonObject object = json.getAsJsonObject();
        String name = object.getAsJsonPrimitive("name").getAsString();
        Stock stock = context.deserialize(object.getAsJsonObject("stock"), Stock.class);

        Map<Object, Object> genericMap = context.deserialize(object.getAsJsonObject("rawDataCounts"), Map.class);
        Map<Class<? extends DataSource>, Integer> typedMap = new HashMap<>();
        for (Map.Entry<Object, Object> entry : genericMap.entrySet())
        {
            try
            {
                Class<?> srcClass = Class.forName((String) entry.getKey());
                double count = (Double) entry.getValue();
                typedMap.put((Class<? extends DataSource>) srcClass, (int) count);
            }
            catch (ClassNotFoundException e)
            {
                Framework.error("Unable to deserialize DataSource Class " + entry.getKey(), e);
            }
        }
        return new TimeSeries(name, stock);
    }


}
