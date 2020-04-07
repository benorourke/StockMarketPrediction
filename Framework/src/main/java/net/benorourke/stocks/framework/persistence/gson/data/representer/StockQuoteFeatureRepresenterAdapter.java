package net.benorourke.stocks.framework.persistence.gson.data.representer;

import com.google.gson.*;
import net.benorourke.stocks.framework.persistence.gson.JsonAdapter;
import net.benorourke.stocks.framework.preprocess.quote.StockQuoteFeatureRepresenter;

import java.lang.reflect.Type;

public class StockQuoteFeatureRepresenterAdapter extends JsonAdapter<StockQuoteFeatureRepresenter>
{

    @Override
    public JsonElement serialize(StockQuoteFeatureRepresenter representer, Type typeOfSrc, JsonSerializationContext context)
    {
        return new JsonObject();
    }

    @Override
    public StockQuoteFeatureRepresenter deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        return new StockQuoteFeatureRepresenter();
    }

}
