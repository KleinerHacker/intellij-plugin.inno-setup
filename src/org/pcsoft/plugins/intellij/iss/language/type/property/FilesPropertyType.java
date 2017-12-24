package org.pcsoft.plugins.intellij.iss.language.type.property;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.type.ValueType;
import org.pcsoft.plugins.intellij.iss.language.type.SectionType;
import org.pcsoft.plugins.intellij.iss.language.type.base.SpecialValueType;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertyType;
import org.pcsoft.plugins.intellij.iss.language.type.base.annotation.*;
import org.pcsoft.plugins.intellij.iss.language.type.value.AttributesValueType;
import org.pcsoft.plugins.intellij.iss.language.type.value.FilesFlagValueType;

public enum FilesPropertyType implements PropertyType {
    @IsRequired @IsKeyProperty
    Source("Source", ValueType.String),
    @IsRequired @IsInfoProperty
    DestDir("DestDir", ValueType.String),
    DestName("DestName", ValueType.String),
    Excludes("Excludes", ValueType.String),
    ExternalSize("ExternalSize", ValueType.Number),
    CopyMode("CopyMode", ValueType.SingleValue),
    Attributes("Attribs", ValueType.MultiValue, AttributesValueType.class),
    Permissions("Permissions", ValueType.SingleValue),
    FontInstall("FontInstall", ValueType.String),
    StrongAssemblyName("StrongAssemblyName", ValueType.String),
    Flags("Flags", ValueType.MultiValue, FilesFlagValueType.class),
    @ReferenceTo(SectionType.Components)
    Components("Components", ValueType.MultiValue),
    @ReferenceTo(SectionType.Tasks)
    Tasks("Tasks", ValueType.MultiValue),
    Languages("Languages", ValueType.MultiValue),
    MinVersion("MinVersion", ValueType.Version),
    OnlyBelowVersion("OnlyBelowVersion", ValueType.Version),
    ;

    private final String name;
    private final ValueType[] valueTypes;
    private final Class<? extends SpecialValueType> propertySpecialValueTypeClass;
    private final boolean required, deprecated;
    private final boolean isKey, isInfo;
    private final boolean isReferenceKey;
    private final SectionType referenceTargetSectionType;

    private FilesPropertyType(String name, ValueType valueType) {
        this(name, new ValueType[]{valueType}, null);
    }

    private FilesPropertyType(String name, ValueType[] valueTypes) {
        this(name, valueTypes, null);
    }

    private FilesPropertyType(String name, ValueType valueType, Class<? extends SpecialValueType> propertySpecialValueTypeClass) {
        this(name, new ValueType[]{valueType}, propertySpecialValueTypeClass);
    }

    private FilesPropertyType(String name, ValueType[] valueTypes, Class<? extends SpecialValueType> propertySpecialValueTypeClass) {
        this.name = name;
        this.valueTypes = valueTypes;
        this.propertySpecialValueTypeClass = propertySpecialValueTypeClass;

        try {
            required = getClass().getField(name()).getAnnotation(IsRequired.class) != null;
            deprecated = getClass().getField(name()).getAnnotation(IsDeprecated.class) != null;

            isKey = getClass().getField(name()).getAnnotation(IsKeyProperty.class) != null;
            isInfo = getClass().getField(name()).getAnnotation(IsInfoProperty.class) != null;

            isReferenceKey = getClass().getField(name()).getAnnotation(IsReferenceKey.class) != null;
            final ReferenceTo referenceToAnnotation = getClass().getField(name()).getAnnotation(ReferenceTo.class);
            referenceTargetSectionType = referenceToAnnotation == null ? null : referenceToAnnotation.value();
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
    public ValueType[] getValueTypes() {
        return valueTypes;
    }

    @Nullable
    @Override
    public Class<? extends SpecialValueType> getSpecialValueTypeClass() {
        return propertySpecialValueTypeClass;
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

    @Override
    public boolean isReferenceKey() {
        return isReferenceKey;
    }

    @Override
    public SectionType getReferenceTargetSectionType() {
        return referenceTargetSectionType;
    }
}
