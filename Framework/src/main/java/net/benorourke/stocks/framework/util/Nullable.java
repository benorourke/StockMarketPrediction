package net.benorourke.stocks.framework.util;

import java.lang.annotation.*;

/**
 * Indicates that a field, parameter or local variable can be null.
 *
 * Additionally indicates that a method can return a null value.
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
public @interface Nullable {}
