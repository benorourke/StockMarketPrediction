package net.benorourke.stocks.framework.util;

import java.util.List;
import java.util.Map;

public interface UserInterfaceInputHook<T>
{

    List<Input> getInputs();

    boolean validate(Input input, Object object);

    T handle(Map<Input, Object> map);

    enum InputType
    {
        INTEGER, STRING
    }

    class Input
    {
        private final String id;
        private final InputType type;

        public Input(String id, InputType type)
        {
            this.id = id;
            this.type = type;
        }

        public String getId()
        {
            return id;
        }

        public InputType getType()
        {
            return type;
        }
    }

}
