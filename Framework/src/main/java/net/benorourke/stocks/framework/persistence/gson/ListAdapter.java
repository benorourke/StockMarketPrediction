package net.benorourke.stocks.framework.persistence.gson;

import com.google.gson.*;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.persistence.gson.JsonAdapter;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;
import net.benorourke.stocks.framework.util.DateUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListAdapter implements JsonAdapter<List<?>>
{

    @Override
    public JsonElement serialize(List<?> list, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonArray result = new JsonArray();

        for (Object elem : list)
        {
            result.add(context.serialize(elem));
        }

        return result;
    }

    @Override
    public List<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        Type valueType = ((ParameterizedType) typeOfT).getActualTypeArguments()[0];

        List<?> list = new ArrayList<>();
        JsonArray array = json.getAsJsonArray();
        Framework.debug("Deserializing " + array.size() + " elements");
        for (JsonElement elem : array)
        {
            list.add(context.deserialize(elem, valueType));

            Framework.debug("About to call to string ");
            Framework.debug("Captured: " + context.deserialize(elem, valueType).toString());
        }
        return list;
    }

}
