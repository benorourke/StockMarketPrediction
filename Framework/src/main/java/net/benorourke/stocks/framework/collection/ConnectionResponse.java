package net.benorourke.stocks.framework.collection;

import com.google.gson.JsonObject;
import net.benorourke.stocks.framework.series.data.Data;

import java.util.List;

public class ConnectionResponse<T extends Data>
{
    private final String rawResponse;
    private final JsonObject parsedResponse;
    private final List<T> data;

    public ConnectionResponse(String rawResponse, JsonObject parsedResponse, List<T> data)
    {
        this.rawResponse = rawResponse;
        this.parsedResponse = parsedResponse;
        this.data = data;
    }

    public String getRawResponse()
    {
        return rawResponse;
    }

    public JsonObject getParsedResponse()
    {
        return parsedResponse;
    }

    public List<T> getData()
    {
        return data;
    }

}
