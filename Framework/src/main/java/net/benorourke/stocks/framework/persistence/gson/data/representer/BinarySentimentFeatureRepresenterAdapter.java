package net.benorourke.stocks.framework.persistence.gson.data.representer;

import com.google.gson.*;
import net.benorourke.stocks.framework.persistence.gson.JsonAdapter;
import net.benorourke.stocks.framework.preprocess.document.representer.sentiment.BinarySentimentFeatureRepresenter;

import java.lang.reflect.Type;

public class BinarySentimentFeatureRepresenterAdapter extends JsonAdapter<BinarySentimentFeatureRepresenter>
{

    @Override
    public JsonElement serialize(BinarySentimentFeatureRepresenter representer, Type typeOfSrc, JsonSerializationContext context)
    {
        return new JsonObject();
    }

    @Override
    public BinarySentimentFeatureRepresenter deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        return new BinarySentimentFeatureRepresenter();
    }

}
