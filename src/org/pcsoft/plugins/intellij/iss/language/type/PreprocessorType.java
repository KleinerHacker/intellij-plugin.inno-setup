package org.pcsoft.plugins.intellij.iss.language.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.type.base.SpecialValueType;
import org.pcsoft.plugins.intellij.iss.language.type.base.annotation.*;
import org.pcsoft.plugins.intellij.iss.language.type.value.PreprocessorTypeValueType;

public enum PreprocessorType implements org.pcsoft.plugins.intellij.iss.language.type.base.PreprocessorType {
    Include("include", ValueType.String),
    PreProc("preproc", ValueType.SingleValue, PreprocessorTypeValueType.class),
    Define("define", new ValueType[] {ValueType.SingleValue, ValueType.String, ValueType.Number}, PreprocessorTypeValueType.class),
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
    public boolean isDeprecated() {
        return deprecated;
    }
}
