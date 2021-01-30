package org.pcsoft.plugins.intellij.iss.core.language.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.core.language.type.base.SpecialValueType;
import org.pcsoft.plugins.intellij.iss.core.language.type.base.annotation.IsDeprecated;
import org.pcsoft.plugins.intellij.iss.core.language.type.base.annotation.IsReferenceKey;
import org.pcsoft.plugins.intellij.iss.core.language.type.value.PreprocessorTypeValueType;

public enum PreprocessorType implements org.pcsoft.plugins.intellij.iss.core.language.type.base.PreprocessorType {
    Include("include", ValueType.String),
    PreProc("preproc", ValueType.String, PreprocessorTypeValueType.class),
    @IsReferenceKey
    Define("define", ValueType.String, ValueType.Number),
    ;

    @Nullable
    public static PreprocessorType fromName(@Nullable final String name) {
        if (name == null)
            return null;

        for (final PreprocessorType preprocessorType : values()) {
            if (preprocessorType.getName().equalsIgnoreCase(name))
                return preprocessorType;
        }

        return null;
    }

    private final String name;
    private final ValueType[] valueTypes;
    private final Class<? extends SpecialValueType> specialValueTypeClass;
    private final boolean referenceKey;
    private final boolean deprecated;

    private PreprocessorType(String name, ValueType... valueTypes) {
        this(name, valueTypes, null);
    }

    private PreprocessorType(String name, ValueType valueType, Class<? extends SpecialValueType> specialValueTypeClass) {
        this(name, new ValueType[]{valueType}, specialValueTypeClass);
    }

    private PreprocessorType(String name, ValueType[] valueTypes, Class<? extends SpecialValueType> specialValueTypeClass) {
        this.name = name;
        this.valueTypes = valueTypes;
        this.specialValueTypeClass = specialValueTypeClass;

        try {
            referenceKey =  getClass().getField(name()).getAnnotation(IsReferenceKey.class) != null;
            deprecated = getClass().getField(name()).getAnnotation(IsDeprecated.class) != null;
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
        return specialValueTypeClass;
    }

    @Override
    public boolean isReferenceKey() {
        return referenceKey;
    }

    @Override
    public boolean isDeprecated() {
        return deprecated;
    }
}
