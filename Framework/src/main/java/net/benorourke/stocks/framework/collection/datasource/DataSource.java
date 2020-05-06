package net.benorourke.stocks.framework.collection.datasource;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.collection.Query;
import net.benorourke.stocks.framework.collection.constraint.Constraint;
import net.benorourke.stocks.framework.collection.datasource.variable.CollectionVariable;
import net.benorourke.stocks.framework.collection.datasource.variable.Validators;
import net.benorourke.stocks.framework.collection.datasource.variable.VariableValidator;
import net.benorourke.stocks.framework.collection.session.APICollectionSession;
import net.benorourke.stocks.framework.collection.session.filter.CollectionFilter;
import net.benorourke.stocks.framework.exception.ConstraintException;
import net.benorourke.stocks.framework.exception.FailedCollectionException;
import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.util.Nullable;
import net.benorourke.stocks.framework.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.*;

public abstract class DataSource<T extends Data>
{
    private final String name;
    private final LinkedHashMap<CollectionVariable, Field> collectionVariables;

    /** Null until {@link #validateCollectionVariables()} is called for the first time. */
    @Nullable
    private Map<CollectionVariable, List<VariableValidator>> variableValidators;

    public DataSource(String name)
    {
        this.name = name;
        this.collectionVariables = ReflectionUtil.getAnnotatedFields(getClass(), CollectionVariable.class);

        for (Map.Entry<CollectionVariable, Field> entry : collectionVariables.entrySet())
            Framework.info("Resolved CollectionVariable " + entry.getKey().name() + " in " + getClass().getSimpleName());
    }

    public abstract Class<? extends T> getDataClass();

    public abstract DataType getDataType();

    public abstract Constraint[] getConstraints();

    public abstract APICollectionSession<T> newSession(Query completeQuery, CollectionFilter<T> collectionFilter);

    public abstract CollectionFilter<T> newDefaultCollectionFilter();

    public abstract Collection<T> retrieve(Query query) throws ConstraintException, FailedCollectionException;

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return (obj instanceof DataSource) ? obj.hashCode() == hashCode() : false;
    }

    /**
     * @return the error with the validation. If null is returned, there were no invalid variables and
     */
    @Nullable
    public String validateCollectionVariables()
    {
        if (variableValidators == null)
            setValidators();

        for (Map.Entry<CollectionVariable, Field> variableEntry : collectionVariables.entrySet())
        {
            CollectionVariable variable = variableEntry.getKey();
            try
            {
                // Check if there's any validators for this variable before we reflect the field value
                if (!variableValidators.containsKey(variable))
                    continue;

                // Reflect the value of this variable
                Object value = variableEntry.getValue().get(this);
                // Loop through the validators for this variable
                for (VariableValidator validator : variableValidators.get(variable))
                {
                    // If the validator rendered this value invalid, return why, otherwise continue
                    if (!validator.isValid(variable, value))
                        return validator.reasonInvalid(variable, value);
                }
            }
            catch (IllegalAccessException e)
            {
                Framework.error("Unable to reflect value of field " + variable.name(), e);
            }
        }

        // All of the fields for this variable are valid
        return null;
    }

    private void setValidators()
    {
        variableValidators = new HashMap<>();

        for (Map.Entry<CollectionVariable, Field> entry : collectionVariables.entrySet())
        {
            variableValidators.put(entry.getKey(), new ArrayList<>());
            for (String validatorName : entry.getKey().validators())
            {
                VariableValidator validator = Validators.getByName(validatorName);
                if (validator == null)
                    Framework.error("Unable to resolve Variable Validator " + validatorName
                                        + " for variable " + entry.getKey().name());
                else
                    variableValidators.get(entry.getKey()).add(validator);
            }
        }
    }

    public void checkConstraintsOrThrow(final Query query) throws ConstraintException
    {
        for (Constraint constraint : getConstraints())
        {
            constraint.checkValid(query);
        }
    }

    public String getName()
    {
        return name;
    }

    public Set<CollectionVariable> getCollectionVariables()
    {
        return collectionVariables.keySet();
    }

    public Object getVariableValue(CollectionVariable variable)
    {
        try
        {
            Field field = collectionVariables.get(variable);
            field.setAccessible(true);
            return field.get(this);
        }
        catch (IllegalAccessException e)
        {
            Framework.error("Unable to get value of Field", e);
            return null;
        }
    }

    public void setVariableValue(CollectionVariable variable, Object value) throws IllegalAccessException
    {
        Field field = collectionVariables.get(variable);
        field.setAccessible(true);
        field.set(this, value);
    }

}
