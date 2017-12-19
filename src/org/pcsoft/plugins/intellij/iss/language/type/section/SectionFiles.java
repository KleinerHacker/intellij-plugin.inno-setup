package org.pcsoft.plugins.intellij.iss.language.type.section;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.type.ValueType;
import org.pcsoft.plugins.intellij.iss.language.type.base.SectionValue;
import org.pcsoft.plugins.intellij.iss.language.type.base.SectionValueType;
import org.pcsoft.plugins.intellij.iss.language.type.value.SectionAttributesValueType;
import org.pcsoft.plugins.intellij.iss.language.type.value.SectionFilesFlagValueType;

public enum SectionFiles implements SectionValue {
    Source("Source", ValueType.String, null, true),
    DestDir("DestDir", ValueType.String, null, true),
    DestName("DestName", ValueType.String, null),
    Excludes("Excludes", ValueType.String, null),
    ExternalSize("ExternalSize", ValueType.Number, null),
    CopyMode("CopyMode", ValueType.SingleValue, null),
    Attributes("Attribs", ValueType.MultiValue, null, false, false, SectionAttributesValueType.class),
    Permissions("Permissions", ValueType.SingleValue, null),
    FontInstall("FontInstall", ValueType.String, null),
    StrongAssemblyName("StrongAssemblyName", ValueType.String, null),
    Flags("Flags", ValueType.MultiValue, null, false, false, SectionFilesFlagValueType.class),
    ;

    private final String name;
    private final ValueType[] valueTypes;
    private final Object defaultValue;
    private final Class<? extends SectionValueType> sectionValueTypeClass;
    private final boolean required, deprecated;

    private SectionFiles(String name, ValueType valueType, Object defaultValue) {
        this(name, new ValueType[]{valueType}, defaultValue, false);
    }

    private SectionFiles(String name, ValueType[] valueTypes, Object defaultValue) {
        this(name, valueTypes, defaultValue, false);
    }

    private SectionFiles(String name, ValueType valueType, Object defaultValue, boolean required) {
        this(name, new ValueType[]{valueType}, defaultValue, required, false);
    }

    private SectionFiles(String name, ValueType[] valueTypes, Object defaultValue, boolean required) {
        this(name, valueTypes, defaultValue, required, false);
    }

    private SectionFiles(String name, ValueType valueType, Object defaultValue, boolean required, boolean deprecated) {
        this(name, new ValueType[]{valueType}, defaultValue, required, deprecated, null);
    }

    private SectionFiles(String name, ValueType[] valueTypes, Object defaultValue, boolean required, boolean deprecated) {
        this(name, valueTypes, defaultValue, required, deprecated, null);
    }

    private SectionFiles(String name, ValueType valueType, Object defaultValue, boolean required, boolean deprecated, Class<? extends SectionValueType> sectionValueTypeClass) {
        this(name, new ValueType[]{valueType}, defaultValue, required, deprecated, sectionValueTypeClass);
    }

    private SectionFiles(String name, ValueType[] valueTypes, Object defaultValue, boolean required, boolean deprecated, Class<? extends SectionValueType> sectionValueTypeClass) {
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
