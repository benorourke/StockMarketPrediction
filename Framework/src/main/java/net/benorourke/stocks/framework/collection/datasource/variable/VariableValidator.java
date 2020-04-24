package net.benorourke.stocks.framework.collection.datasource.variable;

public interface VariableValidator
{

    boolean isValid(CollectionVariable variable, Object value);

    String reasonInvalid(CollectionVariable variable, Object value);

}
