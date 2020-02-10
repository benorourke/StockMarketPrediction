package net.ben.stocks.framework.persistence.gson.data;

import com.google.gson.*;
import net.ben.stocks.framework.persistence.gson.JsonAdapter;
import net.ben.stocks.framework.series.data.Document;
import net.ben.stocks.framework.series.data.DocumentType;
import net.ben.stocks.framework.util.DateUtil;

import java.lang.reflect.Type;
import java.util.Date;

public class DocumentAdapter implements JsonAdapter<Document>
{

    @Override
    public JsonElement serialize(Document document, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject result = new JsonObject();
        result.add("date", new JsonPrimitive(DateUtil.formatDetailed(document.getDate())));
        result.add("content", new JsonPrimitive(document.getContent()));
        result.add("type", new JsonPrimitive(document.getDocumentType().toString()));
        return result;
    }

    @Override
    public Document deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        JsonObject object = json.getAsJsonObject();
        Date date = DateUtil.parseDetailedUK(object.getAsJsonPrimitive("date").getAsString());
        String content = object.getAsJsonPrimitive("content").getAsString();
        DocumentType type = DocumentType.valueOf(object.getAsJsonPrimitive("type").getAsString());
        return new Document(date, content, type);
    }

}
