package net.benorourke.stocks.framework.collection.datasource;

import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.collection.Query;
import net.benorourke.stocks.framework.collection.constraint.Constraint;
import net.benorourke.stocks.framework.collection.constraint.OrderingConstraint;
import net.benorourke.stocks.framework.collection.datasource.variable.CollectionVariable;
import net.benorourke.stocks.framework.collection.datasource.variable.Validators;
import net.benorourke.stocks.framework.collection.datasource.variable.VariableValidator;
import net.benorourke.stocks.framework.collection.session.CollectionSession;
import net.benorourke.stocks.framework.collection.session.filter.CollectionFilter;
import net.benorourke.stocks.framework.exception.ConstraintException;
import net.benorourke.stocks.framework.exception.FailedCollectionException;
import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.util.Nullable;
import net.benorourke.stocks.framework.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.*;

/**
 * A source of data.
 *
 * @param <T> the type of data.
 */
public abstract class DataSource<T extends Data>
{
    private final String name;
    private final LinkedHashMap<CollectionVariable, Field> collectionVariables;

    /** Null until {@link #validateCollectionVariables()} is called for the first time. */
    @Nullable
    private Map<CollectionVariable, List<VariableValidator>> variableValidators;

    /**
     * Create a new instance.
     *
     * @param name the name of the source
     */
    public DataSource(String name)
    {
        this.name = name;
        // Reflect all the possible collection variables
        this.collectionVariables = ReflectionUtil.getAnnotatedFields(getClass(), CollectionVariable.class);

        // Print them to the console
        for (Map.Entry<CollectionVariable, Field> entry : collectionVariables.entrySet())
            Framework.info("Resolved CollectionVariable " + entry.getKey().name() + " in " + getClass().getSimpleName());
    }

    /**
     * Get the class of the data that this source will be collecting.
     * @return the data class
     */
    public abstract Class<? extends T> getDataClass();

    /**
     * Get the type wrapper for the data this source will be collecting.
     * @return the data type
     */
    public abstract DataType getDataType();

    /**
     * Get the constraints that are DataSource-independent.
     *
     * @return the constraints
     */
    public final Constraint[] getCoreConstrants()
    {
        return new Constraint[] { new OrderingConstraint() };
    }

    /**
     * Get the constraints that are DataSource-specific.
     *
     * @return the constraints
     */
    public abstract Constraint[] getConstraints();

    /**
     * Generate a new collection session for this DataSource.
     *
     * @param completeQuery the query for data collection
     * @param collectionFilter the filter to apply to any data collected
     * @return
     */
    public abstract CollectionSession<T> newSession(Query completeQuery, CollectionFilter<T> collectionFilter);

    /**
     * Generate a default collection filter.
     *
     * @return the default collection filter
     */
    public abstract CollectionFilter<T> newDefaultCollectionFilter();

    /**
     * Collect data for a given query.
     * @param query the query
     *
     * @return the returned data
     * @throws ConstraintException if any constraints fail
     * @throws FailedCollectionException if data collection fails for any reason
     */
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
     * Validate all of the reflected collection variables.
     *
     * @return the error with the validation.
 *             If null is returned, there were no invalid variables and it is safe to collect
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

    /**
     * Set the list of validators for each individual CollectionVariable.
     */
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

    /**
     * Check all (core and datasource-specific) constraints.
     *
     * @param query the query to check for
     * @throws ConstraintException if a constraint fails
     */
    public void checkConstraintsOrThrow(final Query query) throws ConstraintException
    {
        // Check the DataSource-independent constraints
        for (Constraint constraint : getCoreConstrants())
            constraint.checkValid(query);

        // Check the DataSource-specific constraints
        for (Constraint constraint : getConstraints())
            constraint.checkValid(query);
    }

    public String getName()
    {
        return name;
    }

    public Set<CollectionVariable> getCollectionVariables()
    {
        return collectionVariables.keySet();
    }

    /**
     * Reflect (get) the value of a CollectionVariable field.
     *
     * @param variable the CollectionVariable
     * @return the value
     */
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

    /**
     * Reflect (set) the value of a CollectionVariable field.
     *
     * @param variable the CollectionVariable
     * @param value the value to set it to
     * @throws IllegalAccessException Reflection Exception
     */
    public void setVariableValue(CollectionVariable variable, Object value) throws IllegalAccessException
    {
        Field field = collectionVariables.get(variable);
        field.setAccessible(true);
        field.set(this, value);
    }

}
