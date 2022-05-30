package ba.kripas.jplag;

import java.lang.reflect.Method;

class ConfigSetterContainer {

    private final Method setter;
    private final Object value;

    public Method getSetter() {
        return setter;
    }

    public Object getValue() {
        return value;
    }

    public ConfigSetterContainer(Method setter, Object value) {
        this.setter = setter;
        this.value = value;
    }
}
