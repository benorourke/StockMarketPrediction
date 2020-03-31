package net.benorourke.stocks.framework.persistence.gson.model;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.benorourke.stocks.framework.model.ModelData;
import net.benorourke.stocks.framework.model.ProcessedCorpus;
import net.benorourke.stocks.framework.persistence.gson.JsonAdapter;

import java.lang.reflect.Type;
import java.util.List;

public class ProcessedCorpusAdapter extends JsonAdapter<ProcessedCorpus>
{

    @Override
    public JsonElement serialize(ProcessedCorpus corpus, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject result = new JsonObject();

        result.add("numFeatures", new JsonPrimitive(corpus.getNumFeatures()));
        result.add("numLabels", new JsonPrimitive(corpus.getNumLabels()));
        result.add("data", context.serialize(corpus.getData()));

        JsonArray topTerms = new JsonArray();
        for (String term : corpus.getTopTerms())
        {
            topTerms.add(new JsonPrimitive(term));
        }
        result.add("topTerms", topTerms);

        return result;
    }

    @Override
    public ProcessedCorpus deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        JsonObject object = json.getAsJsonObject();
        int numFeatures = object.getAsJsonPrimitive("numFeatures").getAsInt();
        int numLabels = object.getAsJsonPrimitive("numLabels").getAsInt();
        List<ModelData> data = context.deserialize(object.getAsJsonArray("data"),
                                                   new TypeToken<List<ModelData>>(){}.getType());

        JsonArray topTermsArray = object.getAsJsonArray("topTerms");
        String[] topTerms = new String[topTermsArray.size()];
        for (int i = 0; i < topTerms.length; i ++)
            topTerms[i] = topTermsArray.get(i).getAsJsonPrimitive().getAsString();

        return new ProcessedCorpus(numFeatures, numLabels, topTerms, data);
    }
}
