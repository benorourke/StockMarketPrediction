package net.benorourke.stocks.framework.exception;

import net.benorourke.stocks.framework.series.data.DataType;

import java.util.List;

/**
 * Exception thrown when pre-processing is attempted with insufficient data present.
 */
public class InsuficcientRawDataException extends Exception
{
    private final List<DataType> missingTypes;

    public InsuficcientRawDataException(List<DataType> missingTypes)
    {
        super("Data cannot be pre-processed with missing data");

        this.missingTypes = missingTypes;
    }

    public List<DataType> getMissing()
    {
        return missingTypes;
    }
}
