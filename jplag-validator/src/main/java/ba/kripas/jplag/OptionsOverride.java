package ba.kripas.jplag;

import java.lang.reflect.Type;
import java.util.List;

public class OptionsOverride {
    private final String setter;
    private final String type;
    private final String value;

    public String getSetter() {
        return setter;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public OptionsOverride(String setter, String type, String value) {
        this.setter = setter;
        this.type = type;
        this.value = value;
    }

}
