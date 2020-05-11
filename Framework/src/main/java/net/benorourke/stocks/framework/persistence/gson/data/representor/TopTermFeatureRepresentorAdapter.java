package net.benorourke.stocks.framework.persistence.gson.data.representor;

import com.google.gson.*;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.persistence.gson.JsonAdapter;
import net.benorourke.stocks.framework.preprocess.document.representor.topterm.RelevancyMetric;
import net.benorourke.stocks.framework.preprocess.document.representor.topterm.TopTermFeatureRepresentor;

import java.lang.reflect.Type;

public class TopTermFeatureRepresentorAdapter extends JsonAdapter<TopTermFeatureRepresentor>
{

    @Override
    public JsonElement serialize(TopTermFeatureRepresentor representor, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject obj = new JsonObject();
        obj.add("relevancyMetricClass",
                new JsonPrimitive(representor.getRelevancyMetric().getClass().getName()));
        obj.add("maxTopTerms", new JsonPrimitive(representor.getMaxTopTerms()));

        JsonArray topTermsArray = new JsonArray();
        for (String topTerm : representor.getTopTerms())
            topTermsArray.add(topTerm);
        obj.add("topTerms", topTermsArray);

        return obj;
    }

    @Override
    public TopTermFeatureRepresentor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        JsonObject object = json.getAsJsonObject();

        String strMetricClazz = object.getAsJsonPrimitive("relevancyMetricClass").getAsString();
        try
        {
            // Reflect the RelevancyMetric used by this top term feature representer
            Class<? extends RelevancyMetric> metricClazz = (Class<? extends RelevancyMetric>) Class.forName(strMetricClazz);
            RelevancyMetric relevancyMetric = metricClazz.newInstance();
            int maxTopTerms = object.getAsJsonPrimitive("maxTopTerms").getAsInt();

            // Load all of the top terms for this top term representer
            JsonArray topTermsArray = object.getAsJsonArray("topTerms");
            String[] topTerms = new String[topTermsArray.size()];
            for (int i = 0; i < topTerms.length; i ++)
                topTerms[i] = topTermsArray.get(i).getAsJsonPrimitive().getAsString();

            TopTermFeatureRepresentor representor = new TopTermFeatureRepresentor(relevancyMetric, maxTopTerms);
            representor.setTopTerms(topTerms);
            return representor;
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException e)
        {
            Framework.error("Unable to resolve RelevancyMetric " + strMetricClazz, e);
            return null;
        }
    }

}
