package net.benorourke.stocks.framework.collection.datasource.variable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Validators
{
    private static final Map<String, VariableValidator> VALIDATORS;

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
