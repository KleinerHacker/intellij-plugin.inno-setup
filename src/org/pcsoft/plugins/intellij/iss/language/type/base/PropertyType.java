package org.pcsoft.plugins.intellij.iss.language.type.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.type.PropertyValueType;

/**
 * Created by Christoph on 02.10.2016.
 */
public interface PropertyType extends TypeBase {
    @NotNull
    PropertyValueType[] getPropertyValueTypes();
    @Nullable
    Class<? extends PropertySpecialValueType> getPropertySpecialValueTypeClass();

    boolean isKey();
    boolean isInfo();

    boolean isReferenceKey();
    SectionType getReferenceTargetSectionType();

    boolean isRequired();
}
