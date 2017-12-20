package org.pcsoft.plugins.intellij.iss.language.type.section;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.type.PropertyValueType;
import org.pcsoft.plugins.intellij.iss.language.type.base.SectionProperty;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertySpecialValueType;
import org.pcsoft.plugins.intellij.iss.language.type.base.annotation.IsDeprecated;
import org.pcsoft.plugins.intellij.iss.language.type.base.annotation.IsInfoProperty;
import org.pcsoft.plugins.intellij.iss.language.type.base.annotation.IsKeyProperty;
import org.pcsoft.plugins.intellij.iss.language.type.base.annotation.IsRequired;
import org.pcsoft.plugins.intellij.iss.language.type.value.PropertyBooleanValueType;
import org.pcsoft.plugins.intellij.iss.language.type.value.PropertySetupCompressionValueType;

/**
 * Created by Christoph on 02.10.2016.
 */
public enum SectionSetupProperty implements SectionProperty {
    //Compiler related
    ASLRCompatible("ASLRCompatible", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    Compression("Compression", PropertyValueType.SingleValue, "lzma2/max", PropertySetupCompressionValueType.class),
    CompressionThreads("CompressionThreads", new PropertyValueType[]{PropertyValueType.SingleValue, PropertyValueType.Number}, "auto"),
    DEPCompatible("DEPCompatible", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    DiskClusterSize("DiskCluster", PropertyValueType.Number, 512),
    DiskSliceSize("DiskSliceSize", new PropertyValueType[]{PropertyValueType.SingleValue, PropertyValueType.Number}, "max"),
    DiskSpanning("DiskSpanning", PropertyValueType.Boolean, false, PropertyBooleanValueType.class),
    //Intsaller related
    AllowCancelDuringInstall("AllowCancelDuringInstall", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    AllowNetworkDrive("AllowNetworkDrive", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    AllowNoIcon("AllowNoIcon", PropertyValueType.Boolean, false, PropertyBooleanValueType.class),
    @IsRequired @IsKeyProperty
    AppName("AppName", PropertyValueType.String, null),
    @IsRequired @IsInfoProperty
    AppVersion("AppVersion", PropertyValueType.SingleValue, null),
    //Cosmetic
    AppCopyright("AppCopyright", PropertyValueType.String, null),
    BackColor("BackColor", new PropertyValueType[]{PropertyValueType.SingleValue, PropertyValueType.Number}, "clBlue"),
    BackColor2("BackColor2", new PropertyValueType[]{PropertyValueType.SingleValue, PropertyValueType.Number}, "clBlack"),;

    private final String name;
    private final PropertyValueType[] propertyValueTypes;
    private final Object defaultValue;
    private final Class<? extends PropertySpecialValueType> sectionValueTypeClass;
    private final boolean required, deprecated;
    private final boolean isKey, isInfo;

    private SectionSetupProperty(String name, PropertyValueType propertyValueType, Object defaultValue) {
        this(name, new PropertyValueType[]{propertyValueType}, defaultValue, null);
    }

    private SectionSetupProperty(String name, PropertyValueType[] propertyValueTypes, Object defaultValue) {
        this(name, propertyValueTypes, defaultValue, null);
    }

    private SectionSetupProperty(String name, PropertyValueType propertyValueType, Object defaultValue, Class<? extends PropertySpecialValueType> sectionValueTypeClass) {
        this(name, new PropertyValueType[]{propertyValueType}, defaultValue, sectionValueTypeClass);
    }

    private SectionSetupProperty(String name, PropertyValueType[] propertyValueTypes, Object defaultValue, Class<? extends PropertySpecialValueType> sectionValueTypeClass) {
        this.name = name;
        this.propertyValueTypes = propertyValueTypes;
        this.defaultValue = defaultValue;
        this.sectionValueTypeClass = sectionValueTypeClass;

        try {
            required = getClass().getField(name()).getAnnotation(IsRequired.class) != null;
            deprecated = getClass().getField(name()).getAnnotation(IsDeprecated.class) != null;

            isKey = getClass().getField(name()).getAnnotation(IsKeyProperty.class) != null;
            isInfo = getClass().getField(name()).getAnnotation(IsInfoProperty.class) != null;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public PropertyValueType[] getPropertyValueTypes() {
        return propertyValueTypes;
    }

    @Nullable
    public Object getDefaultValue() {
        return defaultValue;
    }

    @Nullable
    @Override
    public Class<? extends PropertySpecialValueType> getSectionValueTypeClass() {
        return sectionValueTypeClass;
    }

    @Override
    public boolean isKey() {
        return isKey;
    }

    @Override
    public boolean isInfo() {
        return isInfo;
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
