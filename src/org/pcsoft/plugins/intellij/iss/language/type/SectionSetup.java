package org.pcsoft.plugins.intellij.iss.language.type;

/**
 * Created by Christoph on 02.10.2016.
 */
public enum SectionSetup implements SectionValue{
    AppVersion("AppVersion", ValueType.VALUE, null, true),

    ;

    private final String name;
    private final ValueType valueType;
    private final Object defaultValue;
    private final boolean required, deprecated;

    private SectionSetup(String name, ValueType valueType, Object defaultValue) {
        this(name, valueType, defaultValue, false);
    }

    private SectionSetup(String name, ValueType valueType, Object defaultValue, boolean required) {
        this(name, valueType, defaultValue, required, false);
    }

    private SectionSetup(String name, ValueType valueType, Object defaultValue, boolean required, boolean deprecated) {
        this.name = name;
        this.valueType = valueType;
        this.defaultValue = defaultValue;
        this.required = required;
        this.deprecated = deprecated;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ValueType getValueType() {
        return valueType;
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public boolean isDeprecated() {
        return deprecated;
    }
}
