package net.benorourke.stocks.framework.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class ReflectionUtil
{

    private ReflectionUtil() {}

    /**
     * Get all the annotated fields of a class by the annotation.
     *
     * @param clazz the class
     * @param annotation the annotation to search for
     * @param <T> the inferred tpye
     * @return the mappings of the annotated field instances against their fields
     */
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

    /**
     * Get the first constructor of a class by the annotation.
     *
     * @param clazz the class
     * @param annotationClass the annotation to search for
     * @return the first constructor. Null if absent
     */
    public static Constructor<?> getConstructorByAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass)
    {
        return Arrays.stream(clazz.getConstructors())
                     .filter(c -> c.isAnnotationPresent(annotationClass))
                     .findFirst().get();
    }

}
