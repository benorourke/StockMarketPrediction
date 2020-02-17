package net.benorourke.stocks.framework.persistence.gson;

import com.google.gson.*;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.stock.Stock;

import java.lang.reflect.Type;

public class TimeSeriesAdapter implements JsonAdapter<TimeSeries>
{

    @Override
    public JsonElement serialize(TimeSeries series, Type typeOfSrc, JsonSerializationContext context)
    {
        Framework.debug("Calling TimeSeries Serializer");
        JsonObject result = new JsonObject();
        result.add("name", context.serialize(series.getName()));
        result.add("stock", context.serialize(series.getStock()));
        return result;
    }

    @Override
    public TimeSeries deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        JsonObject object = json.getAsJsonObject();
        String name = object.getAsJsonPrimitive("name").getAsString();
        Stock stock = context.deserialize(object.getAsJsonObject("stock"), Stock.class);
        return new TimeSeries(name, stock);
    }


}
