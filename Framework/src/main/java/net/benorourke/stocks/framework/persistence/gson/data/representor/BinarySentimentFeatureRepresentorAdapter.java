package net.benorourke.stocks.framework.persistence.gson.data.representor;

import com.google.gson.*;
import net.benorourke.stocks.framework.persistence.gson.JsonAdapter;
import net.benorourke.stocks.framework.preprocess.document.representor.sentiment.BinarySentimentFeatureRepresentor;

import java.lang.reflect.Type;

public class BinarySentimentFeatureRepresentorAdapter extends JsonAdapter<BinarySentimentFeatureRepresentor>
{

    @Override
    public JsonElement serialize(BinarySentimentFeatureRepresentor representor, Type typeOfSrc, JsonSerializationContext context)
    {
        return new JsonObject();
    }

    @Override
    public BinarySentimentFeatureRepresentor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        return new BinarySentimentFeatureRepresentor();
    }

}
