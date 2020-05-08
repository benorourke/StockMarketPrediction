package net.benorourke.stocks.framework.persistence.gson.model;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.model.ModelData;
import net.benorourke.stocks.framework.model.ProcessedDataset;
import net.benorourke.stocks.framework.persistence.gson.JsonAdapter;
import net.benorourke.stocks.framework.preprocess.FeatureRepresentor;
import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.impl.CleanedDocument;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProcessedDatasetAdapter extends JsonAdapter<ProcessedDataset>
{

    @Override
    public JsonElement serialize(ProcessedDataset dataset, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject result = new JsonObject();
        result.add("numFeatures", new JsonPrimitive(dataset.getNumFeatures()));
        result.add("numLabels", new JsonPrimitive(dataset.getNumLabels()));
        result.add("documentFeatureRepresentors",
                serializeRepresentors(context, dataset.getDocumentFeatureRepresentors()));
        result.add("quoteFeatureRepresentors",
                serializeRepresentors(context, dataset.getQuoteFeatureRepresentors()));

        result.add("data", context.serialize(dataset.getData()));
        return result;
    }

    @Override
    public ProcessedDataset deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        JsonObject object = json.getAsJsonObject();
        int numFeatures = object.getAsJsonPrimitive("numFeatures").getAsInt();
        int numLabels = object.getAsJsonPrimitive("numLabels").getAsInt();

        List<FeatureRepresentor<CleanedDocument>> documentRepresentors = new ArrayList<>();
        for (FeatureRepresentor representor : deserializeRepresentors(
                object.getAsJsonArray("documentFeatureRepresentors"), context))
            documentRepresentors.add(representor);
        List<FeatureRepresentor<StockQuote>> quoteRepresentors = new ArrayList<>();
        for (FeatureRepresentor representor : deserializeRepresentors(
                object.getAsJsonArray("quoteFeatureRepresentors"), context))
            quoteRepresentors.add(representor);

        List<ModelData> data = context.deserialize(object.getAsJsonArray("data"),
                                                   new TypeToken<List<ModelData>>(){}.getType());

        return new ProcessedDataset(documentRepresentors, quoteRepresentors, numFeatures, numLabels, data);
    }

    private <T extends Data> JsonArray serializeRepresentors(JsonSerializationContext context,
                                                              List<FeatureRepresentor<T>> representors)
    {
        JsonArray array = new JsonArray();

        for (FeatureRepresentor representor : representors)
        {
            JsonObject elem = new JsonObject();
            elem.add("class", new JsonPrimitive(representor.getClass().getName()));
            elem.add("metadata", context.serialize(representor));
        }

        return array;
    }

    private List<FeatureRepresentor> deserializeRepresentors(JsonArray array, JsonDeserializationContext context)
    {
        List<FeatureRepresentor> representors = new ArrayList<>();
        for (int i = 0; i < array.size(); i ++)
        {
            JsonObject obj = array.get(i).getAsJsonObject();

            try
            {
                Class<? extends FeatureRepresentor> clazz = (Class<? extends FeatureRepresentor>)
                        Class.forName(obj.getAsJsonPrimitive("class").getAsString());

                representors.add(context.deserialize(obj, clazz));
            }
            catch (ClassNotFoundException e)
            {
                Framework.error("Unable to instantiate FeatureRepresentor", e);
            }

        }
        return representors;
    }

}
