package net.benorourke.stocks.framework.persistence.gson.model;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.benorourke.stocks.framework.model.ModelData;
import net.benorourke.stocks.framework.model.ProcessedCorpus;
import net.benorourke.stocks.framework.persistence.gson.JsonAdapter;

import java.lang.reflect.Type;
import java.util.List;

public class ProcessedCorpusAdapter implements JsonAdapter<ProcessedCorpus>
{

    @Override
    public JsonElement serialize(ProcessedCorpus corpus, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject result = new JsonObject();
        result.add("data", context.serialize(corpus.getData()));
        return result;
    }

    @Override
    public ProcessedCorpus deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        JsonObject object = json.getAsJsonObject();
        List<ModelData> data = context.deserialize(object.getAsJsonArray("data"),
                                                   new TypeToken<List<ModelData>>(){}.getType());
        return new ProcessedCorpus(data);
    }
}
