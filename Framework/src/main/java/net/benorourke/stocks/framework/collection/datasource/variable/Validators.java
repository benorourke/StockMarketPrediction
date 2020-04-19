package net.benorourke.stocks.framework.collection.datasource.variable;

import java.util.HashMap;
import java.util.Map;

public class Validators
{
    private static final Map<String, VariableValidator> VALIDATORS;

    public static final VariableValidator NOT_NULL = new VariableValidator()
    {
        @Override
        public boolean isValid(CollectionVariable variable, Object value)
        {
            return value == null;
        }

        @Override
        public String reasonInvalid(CollectionVariable variable, Object value)
        {
            return "Variable uninitialised";
        }
    };

    static
    {
        VALIDATORS = new HashMap<>();

        inject("NOT_NULL", NOT_NULL);
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
