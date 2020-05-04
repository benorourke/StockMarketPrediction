package net.benorourke.stocks.framework.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;

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

    public static Constructor<?> getConstructorByAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass)
    {
        return Arrays.stream(clazz.getConstructors())
                     .filter(c -> c.isAnnotationPresent(annotationClass))
                     .findFirst().get();
    }

}
