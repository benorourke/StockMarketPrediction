package net.benorourke.stocks.framework.collection.datasource.variable;

import java.lang.annotation.*;

/**
 * Indicates a variable that needs setting within a DataSource before data can be collected.
 *
 * These fields should be reflected by any UI tool to inject values.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface CollectionVariable
{

    /**
     * A locale name for this variable.
     */
    String name();

    /**
     * @return the type of data stored within this collection variable
     */
    Type type();

    /**
     * @return the string to be displayed by a user interface when requesting data
     */
    String prompt();

    /**
     * The validators keys to mapping stored within within {@link net.benorourke.stocks.framework.collection.datasource.variable.Validators}
     * that this variable must have validated before data can be collected.
     *
     * @return the array of validator keys
     */
    String[] validators() default {};

    enum Type
    {
        INTEGER, STRING
    }

}
