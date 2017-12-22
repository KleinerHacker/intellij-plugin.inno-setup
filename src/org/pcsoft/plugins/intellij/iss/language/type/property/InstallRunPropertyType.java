package org.pcsoft.plugins.intellij.iss.language.type.property;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.type.PropertyValueType;
import org.pcsoft.plugins.intellij.iss.language.type.SectionType;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertySpecialValueType;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertyType;
import org.pcsoft.plugins.intellij.iss.language.type.base.annotation.*;
import org.pcsoft.plugins.intellij.iss.language.type.value.PropertyInstallRunFlagValueType;

public enum InstallRunPropertyType implements PropertyType {
    @IsRequired @IsKeyProperty
    Filename("Filename", PropertyValueType.String),
    @IsInfoProperty
    Description("Description", PropertyValueType.String),
    Parameters("Parameters", PropertyValueType.String),
    WorkingDirectory("WorkingDir", PropertyValueType.String),
    StatusMessage("StatusMsg", PropertyValueType.String),
    Verb("Verb", PropertyValueType.String),
    Flags("Flags", PropertyValueType.MultiValue, PropertyInstallRunFlagValueType.class),
    @ReferenceTo(SectionType.Components)
    Components("Components", PropertyValueType.MultiValue),
    @ReferenceTo(SectionType.Tasks)
    Tasks("Tasks", PropertyValueType.MultiValue),
    Languages("Languages", PropertyValueType.MultiValue),
    MinVersion("MinVersion", PropertyValueType.Version),
    OnlyBelowVersion("OnlyBelowVersion", PropertyValueType.Version),
    ;

    private final String name;
    private final PropertyValueType[] propertyValueTypes;
    private final Class<? extends PropertySpecialValueType> propertySpecialValueTypeClass;
    private final boolean required, deprecated;
    private final boolean isKey, isInfo;
    private final boolean isReferenceKey;
    private final SectionType referenceTargetSectionType;

    private InstallRunPropertyType(String name, PropertyValueType propertyValueType) {
        this(name, new PropertyValueType[]{propertyValueType}, null);
    }

    private InstallRunPropertyType(String name, PropertyValueType[] propertyValueTypes) {
        this(name, propertyValueTypes, null);
    }

    private InstallRunPropertyType(String name, PropertyValueType propertyValueType, Class<? extends PropertySpecialValueType> propertySpecialValueTypeClass) {
        this(name, new PropertyValueType[]{propertyValueType}, propertySpecialValueTypeClass);
    }

    private InstallRunPropertyType(String name, PropertyValueType[] propertyValueTypes, Class<? extends PropertySpecialValueType> propertySpecialValueTypeClass) {
        this.name = name;
        this.propertyValueTypes = propertyValueTypes;
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
    public PropertyValueType[] getPropertyValueTypes() {
        return propertyValueTypes;
    }

    @Nullable
    @Override
    public Class<? extends PropertySpecialValueType> getPropertySpecialValueTypeClass() {
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
