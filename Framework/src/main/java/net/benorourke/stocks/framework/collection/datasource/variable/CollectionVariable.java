package net.benorourke.stocks.framework.collection.datasource.variable;

import net.benorourke.stocks.framework.util.Nullable;

import java.lang.annotation.*;

/**
 * Indicates a variable that needs setting before data can be collected.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface CollectionVariable
{

    String name();

    Type type();

    String prompt();

    String[] validators();

    enum Type
    {
        INTEGER, STRING, LIST_STRING;
    }

}
