package net.benorourke.stocks.framework.series.data;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR})
public @interface RawDataAnnotation
{

    int indexOfDate();

    String[] paramOrder();

}
