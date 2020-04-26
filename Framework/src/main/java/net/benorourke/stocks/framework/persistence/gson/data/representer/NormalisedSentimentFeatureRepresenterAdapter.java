package net.benorourke.stocks.framework.persistence.gson.data.representer;

import com.google.gson.*;
import net.benorourke.stocks.framework.persistence.gson.JsonAdapter;

import java.lang.reflect.Type;

public class NormalisedSentimentFeatureRepresenterAdapter extends JsonAdapter<NormalisedSentimentFeatureRepresenterAdapter>
{

    @Override
    public JsonElement serialize(NormalisedSentimentFeatureRepresenterAdapter representer, Type typeOfSrc, JsonSerializationContext context)
    {
        return new JsonObject();
    }

    @Override
    public NormalisedSentimentFeatureRepresenterAdapter deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        return new NormalisedSentimentFeatureRepresenterAdapter();
    }

}
