package net.benorourke.stocks.framework.persistence.gson.data.representer;

import com.google.gson.*;
import net.benorourke.stocks.framework.persistence.gson.JsonAdapter;
import net.benorourke.stocks.framework.preprocess.document.representer.sentiment.NormalisedSentimentFeatureRepresenter;

import java.lang.reflect.Type;

public class NormalisedSentimentFeatureRepresenterAdapter extends JsonAdapter<NormalisedSentimentFeatureRepresenter>
{

    @Override
    public JsonElement serialize(NormalisedSentimentFeatureRepresenter representer, Type typeOfSrc, JsonSerializationContext context)
    {
        return new JsonObject();
    }

    @Override
    public NormalisedSentimentFeatureRepresenter deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        return new NormalisedSentimentFeatureRepresenter();
    }

}
