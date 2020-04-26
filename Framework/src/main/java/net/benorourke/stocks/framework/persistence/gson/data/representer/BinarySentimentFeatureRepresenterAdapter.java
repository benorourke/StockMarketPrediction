package net.benorourke.stocks.framework.persistence.gson.data.representer;

import com.google.gson.*;
import net.benorourke.stocks.framework.persistence.gson.JsonAdapter;
import net.benorourke.stocks.framework.preprocess.document.representer.sentiment.SentimentFeatureRepresenter;
import net.benorourke.stocks.framework.preprocess.quote.StockQuoteFeatureRepresenter;

import java.lang.reflect.Type;

public class BinarySentimentFeatureRepresenterAdapter extends JsonAdapter<BinarySentimentFeatureRepresenterAdapter>
{

    @Override
    public JsonElement serialize(BinarySentimentFeatureRepresenterAdapter representer, Type typeOfSrc, JsonSerializationContext context)
    {
        return new JsonObject();
    }

    @Override
    public BinarySentimentFeatureRepresenterAdapter deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        return new BinarySentimentFeatureRepresenterAdapter();
    }

}
