package net.benorourke.stocks.framework.persistence.gson.data.representer;

import com.google.gson.*;
import net.benorourke.stocks.framework.persistence.gson.JsonAdapter;
import net.benorourke.stocks.framework.preprocess.quote.StockQuoteFeatureRepresenter;
import net.benorourke.stocks.framework.series.data.impl.StockQuoteDataType;

import java.lang.reflect.Type;

public class StockQuoteFeatureRepresenterAdapter extends JsonAdapter<StockQuoteFeatureRepresenter>
{

    @Override
    public JsonElement serialize(StockQuoteFeatureRepresenter representer, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonArray array = new JsonArray();
        for (StockQuoteDataType dataType : representer.getDataTypes())
            array.add(dataType.name());
        return array;
    }

    @Override
    public StockQuoteFeatureRepresenter deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        JsonArray array = json.getAsJsonArray();
        StockQuoteDataType[] types = new StockQuoteDataType[array.size()];
        for (int i = 0; i < types.length; i ++)
        {
            String name = array.get(i).getAsJsonPrimitive().getAsString();
            types[i] = StockQuoteDataType.valueOf(name);
        }
        return new StockQuoteFeatureRepresenter(types);
    }

}
