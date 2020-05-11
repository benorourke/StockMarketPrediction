package net.benorourke.stocks.framework.collection.datasource.twitter;

import net.benorourke.stocks.framework.collection.constraint.MaximumAgeConstraint;
import net.benorourke.stocks.framework.util.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * The offical Twitter API endpoints.
 */
public enum EndpointType
{
    FREE_7_DAYS("Free: ~7 Days", new MaximumAgeConstraint(TimeUnit.DAYS.toMillis(7))),
    PREMIUM_30_DAYS("Premium: ~30 Days", new MaximumAgeConstraint(TimeUnit.DAYS.toMillis(30))),
    PREMIUM_ALL_TIME("Premium: Full Archive", null);

    /** Simply a display name for the endpoint. */
    private final String locale;
    /**
     * Null if the endpoint has access to the full archive, otherwise the maximum date range is specified in a
     * MaximumAgeConstraint.
     */
    @Nullable
    private final MaximumAgeConstraint ageConstraint;

    EndpointType(String locale, @Nullable MaximumAgeConstraint ageConstraint)
    {
        this.locale = locale;
        this.ageConstraint = ageConstraint;
    }

    public String getLocale()
    {
        return locale;
    }

    public MaximumAgeConstraint getAgeConstraint()
    {
        return ageConstraint;
    }
}