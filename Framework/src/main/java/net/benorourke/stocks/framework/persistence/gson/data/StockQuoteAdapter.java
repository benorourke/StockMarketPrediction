package net.benorourke.stocks.framework.persistence.gson.data;

import com.google.gson.*;
import net.benorourke.stocks.framework.persistence.gson.JsonAdapter;
import net.benorourke.stocks.framework.series.data.StockQuote;
import net.benorourke.stocks.framework.util.DateUtil;

import java.lang.reflect.Type;
import java.util.Date;

public class StockQuoteAdapter implements JsonAdapter<StockQuote>
{

    @Override
    public JsonElement serialize(StockQuote quote, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject result = new JsonObject();
        result.add("date", new JsonPrimitive(DateUtil.formatDetailed(quote.getDate())));
        result.add("open", new JsonPrimitive(quote.getOpen()));
        result.add("close", new JsonPrimitive(quote.getClose()));
        result.add("high", new JsonPrimitive(quote.getHigh()));
        result.add("low", new JsonPrimitive(quote.getLow()));
        result.add("volume", new JsonPrimitive(quote.getVolume()));
        return result;
    }

    @Override
    public StockQuote deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        JsonObject object = json.getAsJsonObject();
        Date date = DateUtil.parseDetailedUK(object.getAsJsonPrimitive("date").getAsString());
        double open = object.getAsJsonPrimitive("open").getAsDouble();
        double close = object.getAsJsonPrimitive("close").getAsDouble();
        double high = object.getAsJsonPrimitive("high").getAsDouble();
        double low = object.getAsJsonPrimitive("low").getAsDouble();
        long volume = object.getAsJsonPrimitive("volume").getAsLong();
        return new StockQuote(date, open, close, high, low, volume);
    }

}
