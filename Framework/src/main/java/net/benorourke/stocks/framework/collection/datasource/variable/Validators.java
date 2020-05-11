package net.benorourke.stocks.framework.collection.datasource.variable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The cache for Collection Variable Validators.
 *
 * If injecting a custom DataSource that requires custom Validators, inject these into {@link net.benorourke.stocks.framework.Configuration}
 * before instantiating the {@link net.benorourke.stocks.framework.Framework} instance.
 */
public class Validators
{
    private static final Map<String, VariableValidator> VALIDATORS;

    /**
     * A variable validator that states that the value must be either a-z, A-Z or 0-9.
     */
    public static final VariableValidator ALPHANUMERIC = new VariableValidator()
    {
        private final Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");

        @Override
        public boolean isValid(CollectionVariable variable, Object value)
        {
            return !pattern.matcher((String) value).find();
        }

        @Override
        public String reasonInvalid(CollectionVariable variable, Object value)
        {
            return "Contains non-alphanumeric characters";
        }
    };

    static
    {
        VALIDATORS = new HashMap<>();

        // Inject the default validators
        inject("ALPHANUMERIC", ALPHANUMERIC);
    }

    private Validators() {}

    public static VariableValidator getByName(String name)
    {
        return VALIDATORS.get(name.toUpperCase());
    }

    public static void inject(String name, VariableValidator validator)
    {
        VALIDATORS.put(name.toUpperCase(), validator);
    }

}
