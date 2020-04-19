package net.benorourke.stocks.framework.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.lang.annotation.Annotation;

public class ReflectionUtil
{

    private ReflectionUtil() {}

    public static <T extends Annotation> LinkedHashMap<T, Field> getAnnotatedFields(
                                                                    Class<?> clazz, Class<? extends T> annotation)
    {
        LinkedHashMap<T, Field> map = new LinkedHashMap<>();

        Class<?> current = clazz;
        while (current != null)
        {
            for (Field field : current.getDeclaredFields())
            {
                if (field.isAnnotationPresent(annotation))
                    map.put(field.getAnnotation(annotation), field);
            }

            current = current.getSuperclass();
        }
        return map;
    }

}
