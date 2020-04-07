package net.benorourke.stocks.framework.persistence.gson.model;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.model.ModelData;
import net.benorourke.stocks.framework.model.ProcessedDataset;
import net.benorourke.stocks.framework.persistence.gson.JsonAdapter;
import net.benorourke.stocks.framework.preprocess.FeatureRepresenter;
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
        result.add("documentFeatureRepresenters",
                serializeRepresenters(context, dataset.getDocumentFeatureRepresenters()));
        result.add("quoteFeatureRepresenters",
                serializeRepresenters(context, dataset.getQuoteFeatureRepresenters()));

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

        List<FeatureRepresenter<CleanedDocument>> documentRepresenters = new ArrayList<>();
        for (FeatureRepresenter representer : deserializeRepresenters(
                object.getAsJsonArray("documentFeatureRepresenters"), context))
            documentRepresenters.add(representer);
        List<FeatureRepresenter<StockQuote>> quoteRepresenters = new ArrayList<>();
        for (FeatureRepresenter representer : deserializeRepresenters(
                object.getAsJsonArray("quoteFeatureRepresenters"), context))
            quoteRepresenters.add(representer);

        List<ModelData> data = context.deserialize(object.getAsJsonArray("data"),
                                                   new TypeToken<List<ModelData>>(){}.getType());

        return new ProcessedDataset(documentRepresenters, quoteRepresenters, numFeatures, numLabels, data);
    }

    private <T extends Data> JsonArray serializeRepresenters(JsonSerializationContext context,
                                                              List<FeatureRepresenter<T>> representers)
    {
        JsonArray array = new JsonArray();

        for (FeatureRepresenter representer : representers)
        {
            JsonObject elem = new JsonObject();
            elem.add("class", new JsonPrimitive(representer.getClass().getName()));
            elem.add("metadata", context.serialize(representer));
        }

        return array;
    }

    private List<FeatureRepresenter> deserializeRepresenters(JsonArray array, JsonDeserializationContext context)
    {
        List<FeatureRepresenter> representers = new ArrayList<>();
        for (int i = 0; i < array.size(); i ++)
        {
            JsonObject obj = array.get(i).getAsJsonObject();

            try
            {
                Class<? extends FeatureRepresenter> clazz = (Class<? extends FeatureRepresenter>)
                        Class.forName(obj.getAsJsonPrimitive("class").getAsString());

                representers.add(context.deserialize(obj, clazz));
            }
            catch (ClassNotFoundException e)
            {
                Framework.error("Unable to instantiate FeatureRepresenter", e);
            }

        }
        return representers;
    }

}
