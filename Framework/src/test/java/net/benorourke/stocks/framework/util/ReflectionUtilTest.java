package net.benorourke.stocks.framework.util;

import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ReflectionUtilTest
{

    @Test
    public void getAnnotatedFields_UsingDummyClassWithAnnotation_ShouldReturn1()
    {
        assertEquals(1,
                     ReflectionUtil.getAnnotatedFields(DummyClass.class, DummyFieldAnnotation.class).size());
    }

    @Test
    public void getAnnotatedFields_UsingDummyClassWithWrongAnnotation_ShouldReturn0()
    {
        assertEquals(0,
                     ReflectionUtil.getAnnotatedFields(DummyClass.class, DummyUnusedFieldAnnotation.class).size());
    }

    @Test
    public void getConstructorByAnnotation_UsingDummyClassWithAnnotation_ShouldReturnNotNull()
    {
        assertNotEquals(null,
                ReflectionUtil.getConstructorByAnnotation(DummyClass.class, DummyConstructorAnnotation.class));
    }

    @Test (expected = NoSuchElementException.class)
    public void getConstructorByAnnotation_UsingDummyClassWithWrongAnnotation_ShouldThrowException()
    {
        ReflectionUtil.getConstructorByAnnotation(DummyClass.class, DummyUnusedConstructorAnnotation.class);
    }

    private class DummyClass
    {
        @DummyFieldAnnotation
        private int foo;

        @DummyConstructorAnnotation
        public DummyClass() { }

    }

    @Target({ElementType.CONSTRUCTOR})
    @Retention(RetentionPolicy.RUNTIME)
    private @interface DummyConstructorAnnotation { }

    @Target({ElementType.CONSTRUCTOR})
    @Retention(RetentionPolicy.RUNTIME)
    private @interface DummyUnusedConstructorAnnotation { }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    private @interface DummyFieldAnnotation { }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    private @interface DummyUnusedFieldAnnotation { }

}
