package net.benorourke.stocks.framework.persistence.gson;

import com.google.gson.*;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.stock.Stock;
import net.benorourke.stocks.framework.stock.StockExchangeManager;

import java.lang.reflect.Type;

/**
 * Stock contains a cyclic reference to it's member and containing object; StockExchange, so a custom adapter must be
 * specified and registered to Gson.
 */
public class StockAdapter extends JsonAdapter<Stock>
{
    private final StockExchangeManager exchangeManager;

    public StockAdapter(StockExchangeManager exchangeManager)
    {
        this.exchangeManager = exchangeManager;
    }

    @Override
    public JsonElement serialize(Stock stock, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject result = new JsonObject();
        result.add("exchangeName", new JsonPrimitive(stock.getExchange().getName()));
        result.add("companyName", new JsonPrimitive(stock.getCompanyName()));
        result.add("ticker", new JsonPrimitive(stock.getTicker()));
        return result;
    }

    @Override
    public Stock deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        JsonObject object = json.getAsJsonObject();

        String exchangeName = object.getAsJsonPrimitive("exchangeName").getAsString();
        String companyName = object.getAsJsonPrimitive("companyName").getAsString();
        String ticker = object.getAsJsonPrimitive("ticker").getAsString();

        return new Stock(exchangeManager.getByName(exchangeName), companyName, ticker);
    }

}
