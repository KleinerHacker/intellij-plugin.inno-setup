package org.pcsoft.plugins.intellij.iss.language.type.property;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.type.ValueType;
import org.pcsoft.plugins.intellij.iss.language.type.SectionType;
import org.pcsoft.plugins.intellij.iss.language.type.base.SpecialValueType;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertyType;
import org.pcsoft.plugins.intellij.iss.language.type.base.annotation.*;
import org.pcsoft.plugins.intellij.iss.language.type.value.RegistryFlagValueType;
import org.pcsoft.plugins.intellij.iss.language.type.value.RegistryRootValueType;
import org.pcsoft.plugins.intellij.iss.language.type.value.RegistryTypeValueType;

/**
 * Created by Christoph on 02.10.2016.
 */
public enum RegistryPropertyType implements PropertyType {
    @IsRequired @IsKeyProperty
    Root("Root", org.pcsoft.plugins.intellij.iss.language.type.ValueType.SingleValue, RegistryRootValueType.class),
    @IsRequired @IsInfoProperty
    Subkey("Subkey", org.pcsoft.plugins.intellij.iss.language.type.ValueType.String),
    ValueType("ValueType", org.pcsoft.plugins.intellij.iss.language.type.ValueType.SingleValue, RegistryTypeValueType.class),
    ValueName("ValueName", org.pcsoft.plugins.intellij.iss.language.type.ValueType.String),
    ValueData("ValueData", new ValueType[] {org.pcsoft.plugins.intellij.iss.language.type.ValueType.String, org.pcsoft.plugins.intellij.iss.language.type.ValueType.Number, org.pcsoft.plugins.intellij.iss.language.type.ValueType.ByteArray}),
    Permissions("Permissions", org.pcsoft.plugins.intellij.iss.language.type.ValueType.SingleValue),
    Flags("Flags", org.pcsoft.plugins.intellij.iss.language.type.ValueType.SingleValue, RegistryFlagValueType.class),
    @ReferenceTo(SectionType.Components)
    Components("Components", org.pcsoft.plugins.intellij.iss.language.type.ValueType.MultiValue),
    @ReferenceTo(SectionType.Tasks)
    Tasks("Tasks", org.pcsoft.plugins.intellij.iss.language.type.ValueType.MultiValue),
    Languages("Languages", org.pcsoft.plugins.intellij.iss.language.type.ValueType.MultiValue),
    MinVersion("MinVersion", org.pcsoft.plugins.intellij.iss.language.type.ValueType.Version),
    OnlyBelowVersion("OnlyBelowVersion", org.pcsoft.plugins.intellij.iss.language.type.ValueType.Version),
    ;

    private final String name;
    private final org.pcsoft.plugins.intellij.iss.language.type.ValueType[] valueTypes;
    private final Class<? extends SpecialValueType> propertySpecialValueTypeClass;
    private final boolean required, deprecated;
    private final boolean isKey, isInfo;
    private final boolean isReferenceKey;
    private final SectionType referenceTargetSectionType;

    private RegistryPropertyType(String name, org.pcsoft.plugins.intellij.iss.language.type.ValueType valueType) {
        this(name, new ValueType[]{valueType}, null);
    }

    private RegistryPropertyType(String name, org.pcsoft.plugins.intellij.iss.language.type.ValueType[] valueTypes) {
        this(name, valueTypes, null);
    }

    private RegistryPropertyType(String name, org.pcsoft.plugins.intellij.iss.language.type.ValueType valueType, Class<? extends SpecialValueType> propertySpecialValueTypeClass) {
        this(name, new ValueType[]{valueType}, propertySpecialValueTypeClass);
    }

    private RegistryPropertyType(String name, org.pcsoft.plugins.intellij.iss.language.type.ValueType[] valueTypes, Class<? extends SpecialValueType> propertySpecialValueTypeClass) {
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
    public org.pcsoft.plugins.intellij.iss.language.type.ValueType[] getValueTypes() {
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
