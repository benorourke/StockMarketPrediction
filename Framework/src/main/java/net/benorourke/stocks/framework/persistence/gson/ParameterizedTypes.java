package net.benorourke.stocks.framework.persistence.gson;

import com.google.common.reflect.TypeToken;
import net.benorourke.stocks.framework.series.data.impl.Document;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Used to prevent type-erasure while loading using GSON.
 */
public class ParameterizedTypes
{
    public static final ParameterizedType LIST_STOCKQUOTE = new ListParameterizedType(StockQuote.class);
    public static final ParameterizedType LIST_DOCUMENT = new ListParameterizedType(Document.class);

    private ParameterizedTypes() {}

    private static class ListParameterizedType implements ParameterizedType
    {
        private Type type;

        public ListParameterizedType(Type type)
        {
            this.type = type;
        }

        public ListParameterizedType(Class clazz)
        {
            this(TypeToken.of(clazz).getType());
        }

        @Override
        public Type[] getActualTypeArguments()
        {
            return new Type[] {type};
        }

        @Override
        public Type getRawType()
        {
            return ArrayList.class;
        }

        @Override
        public Type getOwnerType()
        {
            return null;
        }
    }

}
