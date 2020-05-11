package net.benorourke.stocks.framework.series.data;
import java.lang.annotation.*;

/**
 * Specifies this constructor should be used when injecting data into the cache without knowing the structure
 * of the raw data.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR})
public @interface RawDataConstructorAnnotation
{

    /**
     * The index of the constructor parameters where the date of the data should go.
     * Typically this should be 0.
     *
     * @return the index
     */
    int indexOfDate();

    /**
     * The order of parameters specified by the {@link RawDataElementAnnotation} annotations
     *
     * @return the parameters, in order
     */
    String[] paramOrder();

}
