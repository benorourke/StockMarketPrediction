package net.benorourke.stocks.framework.persistence.gson.data.representor;

import com.google.gson.*;
import net.benorourke.stocks.framework.persistence.gson.JsonAdapter;
import net.benorourke.stocks.framework.preprocess.document.representor.sentiment.NormalisedSentimentFeatureRepresentor;

import java.lang.reflect.Type;

public class NormalisedSentimentFeatureRepresentorAdapter extends JsonAdapter<NormalisedSentimentFeatureRepresentor>
{

    @Override
    public JsonElement serialize(NormalisedSentimentFeatureRepresentor representor, Type typeOfSrc, JsonSerializationContext context)
    {
        return new JsonObject();
    }

    @Override
    public NormalisedSentimentFeatureRepresentor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        return new NormalisedSentimentFeatureRepresentor();
    }

}
