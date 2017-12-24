package org.pcsoft.plugins.intellij.iss.language.type.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.type.ValueType;

public interface PreprocessorType extends TypeBase {
    @NotNull
    ValueType[] getValueTypes();
    @Nullable
    Class<? extends SpecialValueType> getSpecialValueTypeClass();
}
