package org.pcsoft.plugins.intellij.iss.language.type.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.type.ValueType;

/**
 * Created by Christoph on 02.10.2016.
 */
public interface PropertyType extends TypeBase {
    @NotNull
    ValueType[] getValueTypes();
    @Nullable
    Class<? extends SpecialValueType> getSpecialValueTypeClass();

    boolean isKey();
    boolean isInfo();

    boolean isReferenceKey();
    SectionType getReferenceTargetSectionType();

    boolean isRequired();
}
