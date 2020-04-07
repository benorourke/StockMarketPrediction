package net.benorourke.stocks.framework.persistence.gson.data.representer;

import com.google.gson.*;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.persistence.gson.JsonAdapter;
import net.benorourke.stocks.framework.preprocess.document.representer.sentiment.SentimentFeatureRepresenter;
import net.benorourke.stocks.framework.preprocess.document.representer.topterm.RelevancyMetric;
import net.benorourke.stocks.framework.preprocess.document.representer.topterm.TopTermFeatureRepresenter;

import java.lang.reflect.Type;

public class TopTermFeatureRepresenterAdapter extends JsonAdapter<TopTermFeatureRepresenter>
{

    @Override
    public JsonElement serialize(TopTermFeatureRepresenter representer, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject obj = new JsonObject();
        obj.add("relevancyMetricClass",
                new JsonPrimitive(representer.getRelevancyMetric().getClass().getName()));
        obj.add("maxTopTerms", new JsonPrimitive(representer.getMaxTopTerms()));

        JsonArray topTermsArray = new JsonArray();
        for (String topTerm : representer.getTopTerms())
            topTermsArray.add(topTerm);
        obj.add("topTerms", topTermsArray);

        return obj;
    }

    @Override
    public TopTermFeatureRepresenter deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        JsonObject object = json.getAsJsonObject();

        String strMetricClazz = object.getAsJsonPrimitive("relevancyMetricClass").getAsString();
        try
        {
            Class<? extends RelevancyMetric> metricClazz = (Class<? extends RelevancyMetric>) Class.forName(strMetricClazz);
            RelevancyMetric relevancyMetric = metricClazz.newInstance();
            int maxTopTerms = object.getAsJsonPrimitive("maxTopTerms").getAsInt();

            JsonArray topTermsArray = object.getAsJsonArray("topTerms");
            String[] topTerms = new String[topTermsArray.size()];
            for (int i = 0; i < topTerms.length; i ++)
                topTerms[i] = topTermsArray.get(i).getAsJsonPrimitive().getAsString();

            TopTermFeatureRepresenter representer = new TopTermFeatureRepresenter(relevancyMetric, maxTopTerms);
            representer.setTopTerms(topTerms);
            return representer;
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException e)
        {
            Framework.error("Unable to resolve RelevancyMetric " + strMetricClazz, e);
            return null;
        }
    }

}
