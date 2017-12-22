package org.pcsoft.plugins.intellij.iss.language.type.property;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.type.PropertyValueType;
import org.pcsoft.plugins.intellij.iss.language.type.SectionType;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertySpecialValueType;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertyType;
import org.pcsoft.plugins.intellij.iss.language.type.base.annotation.*;
import org.pcsoft.plugins.intellij.iss.language.type.value.PropertyBooleanValueType;

/**
 * Created by Christoph on 02.10.2016.
 */
public enum LangOptionsPropertyType implements PropertyType {
    @IsRequired @IsKeyProperty
    LanguageName("LanguageName", PropertyValueType.String),
    @IsRequired @IsInfoProperty
    LanguageID("LanguageID", PropertyValueType.Number),
    @IsRequired
    LanguageCodePage("LanguageCodePage", PropertyValueType.Number),
    DialogFontName("DialogFontName", PropertyValueType.String),
    DialogFontSize("DialogFontSize", PropertyValueType.Number),
    WelcomeFontName("WelcomeFontName", PropertyValueType.String),
    WelcomeFontSize("WelcomeFontSize", PropertyValueType.Number),
    TitleFontName("TitleFontName", PropertyValueType.String),
    TitleFontSize("TitleFontSize", PropertyValueType.Number),
    CopyrightFontName("CopyrightFontName", PropertyValueType.String),
    CopyrightFontSize("CopyrightFontSize", PropertyValueType.Number),
    RightToLeft("RightToLeft", PropertyValueType.Boolean, PropertyBooleanValueType.class),
    ;

    private final String name;
    private final PropertyValueType[] propertyValueTypes;
    private final Class<? extends PropertySpecialValueType> propertySpecialValueTypeClass;
    private final boolean required, deprecated;
    private final boolean isKey, isInfo;
    private final boolean isReferenceKey;
    private final SectionType referenceTargetSectionType;

    private LangOptionsPropertyType(String name, PropertyValueType propertyValueType) {
        this(name, new PropertyValueType[]{propertyValueType}, null);
    }

    private LangOptionsPropertyType(String name, PropertyValueType[] propertyValueTypes) {
        this(name, propertyValueTypes, null);
    }

    private LangOptionsPropertyType(String name, PropertyValueType propertyValueType, Class<? extends PropertySpecialValueType> propertySpecialValueTypeClass) {
        this(name, new PropertyValueType[]{propertyValueType}, propertySpecialValueTypeClass);
    }

    private LangOptionsPropertyType(String name, PropertyValueType[] propertyValueTypes, Class<? extends PropertySpecialValueType> propertySpecialValueTypeClass) {
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
