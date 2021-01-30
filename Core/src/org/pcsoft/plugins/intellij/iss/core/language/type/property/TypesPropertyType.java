package org.pcsoft.plugins.intellij.iss.core.language.type.property;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.core.language.type.SectionType;
import org.pcsoft.plugins.intellij.iss.core.language.type.ValueType;
import org.pcsoft.plugins.intellij.iss.core.language.type.base.PropertyType;
import org.pcsoft.plugins.intellij.iss.core.language.type.base.SpecialValueType;
import org.pcsoft.plugins.intellij.iss.core.language.type.base.annotation.*;
import org.pcsoft.plugins.intellij.iss.core.language.type.value.TypesFlagValueType;

/**
 * Created by Christoph on 02.10.2016.
 */
public enum TypesPropertyType implements PropertyType {
    @IsRequired @IsKeyProperty @IsReferenceKey
    Name("Name", ValueType.SingleValue, ValueType.String),
    @IsRequired @IsInfoProperty
    Description("Description", ValueType.String),
    Flags("Flags", ValueType.MultiValue, TypesFlagValueType.class),
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

    private TypesPropertyType(String name, ValueType... valueType) {
        this(name, valueType, null);
    }

    private TypesPropertyType(String name, ValueType valueType, Class<? extends SpecialValueType> propertySpecialValueTypeClass) {
        this(name, new ValueType[]{valueType}, propertySpecialValueTypeClass);
    }

    private TypesPropertyType(String name, ValueType[] valueTypes, Class<? extends SpecialValueType> propertySpecialValueTypeClass) {
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
