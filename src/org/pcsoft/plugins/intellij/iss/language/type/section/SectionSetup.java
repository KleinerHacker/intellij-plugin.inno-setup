package org.pcsoft.plugins.intellij.iss.language.type.section;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.type.ValueType;
import org.pcsoft.plugins.intellij.iss.language.type.base.SectionValue;
import org.pcsoft.plugins.intellij.iss.language.type.base.SectionValueType;
import org.pcsoft.plugins.intellij.iss.language.type.value.SectionBooleanValueType;
import org.pcsoft.plugins.intellij.iss.language.type.value.SectionSetupCompressionValueType;

/**
 * Created by Christoph on 02.10.2016.
 */
public enum SectionSetup implements SectionValue {
    //Compiler related
    ASLRCompatible("ASLRCompatible", ValueType.Bool, true, false, false, SectionBooleanValueType.class),
    Compression("Compression", ValueType.SingleValue, "lzma2/max", false, false, SectionSetupCompressionValueType.class),
    CompressionThreads("CompressionThreads", new ValueType[]{ValueType.SingleValue, ValueType.Number}, "auto"),
    DEPCompatible("DEPCompatible", ValueType.Bool, true, false, false, SectionBooleanValueType.class),
    DiskClusterSize("DiskCluster", ValueType.Number, 512),
    DiskSliceSize("DiskSliceSize", new ValueType[]{ValueType.SingleValue, ValueType.Number}, "max"),
    DiskSpanning("DiskSpanning", ValueType.Bool, false, false, false, SectionBooleanValueType.class),
    //Intsaller related
    AllowCancelDuringInstall("AllowCancelDuringInstall", ValueType.Bool, true, false, false, SectionBooleanValueType.class),
    AllowNetworkDrive("AllowNetworkDrive", ValueType.Bool, true, false, false, SectionBooleanValueType.class),
    AllowNoIcon("AllowNoIcon", ValueType.Bool, false, false, false, SectionBooleanValueType.class),
    AppName("AppName", ValueType.String, null, true),
    AppVersion("AppVersion", ValueType.SingleValue, null, true),
    //Cosmetic
    AppCopyright("AppCopyright", ValueType.String, null),
    BackColor("BackColor", new ValueType[]{ValueType.SingleValue, ValueType.Color}, "clBlue"),
    BackColor2("BackColor2", new ValueType[]{ValueType.SingleValue, ValueType.Color}, "clBlack"),
    ;

    private final String name;
    private final ValueType[] valueTypes;
    private final Object defaultValue;
    private final Class<? extends SectionValueType> sectionValueTypeClass;
    private final boolean required, deprecated;

    private SectionSetup(String name, ValueType valueType, Object defaultValue) {
        this(name, new ValueType[]{valueType}, defaultValue, false);
    }

    private SectionSetup(String name, ValueType[] valueTypes, Object defaultValue) {
        this(name, valueTypes, defaultValue, false);
    }

    private SectionSetup(String name, ValueType valueType, Object defaultValue, boolean required) {
        this(name, new ValueType[]{valueType}, defaultValue, required, false);
    }

    private SectionSetup(String name, ValueType[] valueTypes, Object defaultValue, boolean required) {
        this(name, valueTypes, defaultValue, required, false);
    }

    private SectionSetup(String name, ValueType valueType, Object defaultValue, boolean required, boolean deprecated) {
        this(name, new ValueType[]{valueType}, defaultValue, required, deprecated, null);
    }

    private SectionSetup(String name, ValueType[] valueTypes, Object defaultValue, boolean required, boolean deprecated) {
        this(name, valueTypes, defaultValue, required, deprecated, null);
    }

    private SectionSetup(String name, ValueType valueType, Object defaultValue, boolean required, boolean deprecated, Class<? extends SectionValueType> sectionValueTypeClass) {
        this(name, new ValueType[]{valueType}, defaultValue, required, deprecated, sectionValueTypeClass);
    }

    private SectionSetup(String name, ValueType[] valueTypes, Object defaultValue, boolean required, boolean deprecated, Class<? extends SectionValueType> sectionValueTypeClass) {
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
