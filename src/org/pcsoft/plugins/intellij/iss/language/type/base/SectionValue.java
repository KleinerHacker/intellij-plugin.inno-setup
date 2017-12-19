package org.pcsoft.plugins.intellij.iss.language.type.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.type.ValueType;

/**
 * Created by Christoph on 02.10.2016.
 */
public interface SectionValue extends SectionBase {
    @NotNull
    String getName();
    @NotNull
    ValueType[] getValueTypes();
    @Nullable
    Object getDefaultValue();
    @Nullable
    Class<? extends SectionValueType> getSectionValueTypeClass();
}
