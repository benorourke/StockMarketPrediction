package net.benorourke.stocks.framework.series.data;

import java.lang.annotation.*;

/**
 * An annotation that indicates that a field should be reflected & modified when injecting raw data without knowing
 * it's underlying structure.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface RawDataElementAnnotation
{ }
