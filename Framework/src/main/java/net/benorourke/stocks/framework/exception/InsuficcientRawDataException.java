package net.benorourke.stocks.framework.exception;

import net.benorourke.stocks.framework.series.data.DataType;

import java.util.List;

public class InsuficcientRawDataException extends Exception
{
    private final List<DataType> missingTypes;

    public InsuficcientRawDataException(List<DataType> missingTypes)
    {
        super("ModelData cannot be pre-processed with missing data");

        this.missingTypes = missingTypes;
    }

    public List<DataType> getMissing()
    {
        return missingTypes;
    }
}
