package org.pcsoft.plugins.intellij.iss.language.type.section;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.type.ValueType;
import org.pcsoft.plugins.intellij.iss.language.type.base.SectionValue;
import org.pcsoft.plugins.intellij.iss.language.type.base.SectionValueType;
import org.pcsoft.plugins.intellij.iss.language.type.value.SectionTypesFlagValueType;

/**
 * Created by Christoph on 02.10.2016.
 */
public enum SectionTypes implements SectionValue {
    //Compiler related
    Name("Name", ValueType.SingleValue, null, true),
    Description("Description", ValueType.String, null, true),
    Flags("Flags", ValueType.MultiValue, null, false, false, SectionTypesFlagValueType.class),
    ;

    private final String name;
    private final ValueType[] valueTypes;
    private final Object defaultValue;
    private final Class<? extends SectionValueType> sectionValueTypeClass;
    private final boolean required, deprecated;

    private SectionTypes(String name, ValueType valueType, Object defaultValue) {
        this(name, new ValueType[]{valueType}, defaultValue, false);
    }

    private SectionTypes(String name, ValueType[] valueTypes, Object defaultValue) {
        this(name, valueTypes, defaultValue, false);
    }

    private SectionTypes(String name, ValueType valueType, Object defaultValue, boolean required) {
        this(name, new ValueType[]{valueType}, defaultValue, required, false);
    }

    private SectionTypes(String name, ValueType[] valueTypes, Object defaultValue, boolean required) {
        this(name, valueTypes, defaultValue, required, false);
    }

    private SectionTypes(String name, ValueType valueType, Object defaultValue, boolean required, boolean deprecated) {
        this(name, new ValueType[]{valueType}, defaultValue, required, deprecated, null);
    }

    private SectionTypes(String name, ValueType[] valueTypes, Object defaultValue, boolean required, boolean deprecated) {
        this(name, valueTypes, defaultValue, required, deprecated, null);
    }

    private SectionTypes(String name, ValueType valueType, Object defaultValue, boolean required, boolean deprecated, Class<? extends SectionValueType> sectionValueTypeClass) {
        this(name, new ValueType[]{valueType}, defaultValue, required, deprecated, sectionValueTypeClass);
    }

    private SectionTypes(String name, ValueType[] valueTypes, Object defaultValue, boolean required, boolean deprecated, Class<? extends SectionValueType> sectionValueTypeClass) {
        this.name = name;
        this.valueTypes = valueTypes;
        this.defaultValue = defaultValue;
        this.required = required;
        this.deprecated = deprecated;
        this.sectionValueTypeClass = sectionValueTypeClass;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public ValueType[] getValueTypes() {
        return valueTypes;
    }

    @Nullable
    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    @Nullable
    @Override
    public Class<? extends SectionValueType> getSectionValueTypeClass() {
        return sectionValueTypeClass;
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
