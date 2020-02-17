package net.benorourke.stocks.framework.persistence.gson;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

public interface JsonAdapter<T> extends JsonSerializer<T>, JsonDeserializer<T> {}
