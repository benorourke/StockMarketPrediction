package net.benorourke.stocks.framework.collection.datasource.variable;

/**
 * An interface, used in conjunction with reflection, that will validate a given CollectionVariable.
 */
public interface VariableValidator
{

    /**
     * Determine whether a variable is valid.
     *
     * If returning false, then {@link #reasonInvalid(CollectionVariable, Object)} should respond with a corresponding
     * NOT-NULL reason.
     *
     * @param variable the CollectionVariable annotation
     * @param value the value of the reflected field
     * @return whether it is invalid.
     */
    boolean isValid(CollectionVariable variable, Object value);

    /**
     * The reason why {@link #isValid(CollectionVariable, Object)} has returned false for a corresponding CollectionVariable
     * instance and value.
     *
     * @param variable the CollectionVariable instance
     * @param value the value
     * @return the reason for invalidity
     */
    String reasonInvalid(CollectionVariable variable, Object value);

}
