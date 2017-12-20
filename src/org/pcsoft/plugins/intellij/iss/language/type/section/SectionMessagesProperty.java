package org.pcsoft.plugins.intellij.iss.language.type.section;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.type.PropertyValueType;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertySpecialValueType;
import org.pcsoft.plugins.intellij.iss.language.type.base.SectionProperty;
import org.pcsoft.plugins.intellij.iss.language.type.base.annotation.IsDeprecated;
import org.pcsoft.plugins.intellij.iss.language.type.base.annotation.IsInfoProperty;
import org.pcsoft.plugins.intellij.iss.language.type.base.annotation.IsKeyProperty;
import org.pcsoft.plugins.intellij.iss.language.type.base.annotation.IsRequired;

/**
 * Created by Christoph on 02.10.2016.
 */
public enum SectionMessagesProperty implements SectionProperty {
    ;

    private final String name;
    private final PropertyValueType[] propertyValueTypes;
    private final Class<? extends PropertySpecialValueType> sectionValueTypeClass;
    private final boolean required, deprecated;
    private final boolean isKey, isInfo;

    private SectionMessagesProperty(String name, PropertyValueType propertyValueType) {
        this(name, new PropertyValueType[]{propertyValueType}, null);
    }

    private SectionMessagesProperty(String name, PropertyValueType[] propertyValueTypes) {
        this(name, propertyValueTypes, null);
    }

    private SectionMessagesProperty(String name, PropertyValueType propertyValueType, Class<? extends PropertySpecialValueType> sectionValueTypeClass) {
        this(name, new PropertyValueType[]{propertyValueType}, sectionValueTypeClass);
    }

    private SectionMessagesProperty(String name, PropertyValueType[] propertyValueTypes, Class<? extends PropertySpecialValueType> sectionValueTypeClass) {
        this.name = name;
        this.propertyValueTypes = propertyValueTypes;
        this.sectionValueTypeClass = sectionValueTypeClass;

        try {
            required = getClass().getField(name()).getAnnotation(IsRequired.class) != null;
            deprecated = getClass().getField(name()).getAnnotation(IsDeprecated.class) != null;

            isKey = getClass().getField(name()).getAnnotation(IsKeyProperty.class) != null;
            isInfo = getClass().getField(name()).getAnnotation(IsInfoProperty.class) != null;
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
    public Class<? extends PropertySpecialValueType> getSectionValueTypeClass() {
        return sectionValueTypeClass;
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
}
