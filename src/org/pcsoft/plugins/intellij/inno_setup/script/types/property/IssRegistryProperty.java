package org.pcsoft.plugins.intellij.inno_setup.script.types.property;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssValueType;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssRegistryProperty implements IssPropertyIdentifier {
    Root("Root", IssMarkerFactory.RegistrySection.PROPERTY_ROOT, IssMarkerFactory.RegistrySection.PROPERTY_ROOT_VALUE,
            "registry.property.root", IssValueType.DirectSingle, true),
    Subkey("Subkey", IssMarkerFactory.RegistrySection.PROPERTY_SUBKEY, IssMarkerFactory.RegistrySection.PROPERTY_SUBKEY_VALUE,
            "registry.property.subkey", IssValueType.String, true),
    ValueType("ValueType", IssMarkerFactory.RegistrySection.PROPERTY_VALUETYPE, IssMarkerFactory.RegistrySection.PROPERTY_VALUETYPE_VALUE,
            "registry.property.valuetype", IssValueType.DirectSingle),
    ValueName("ValueName", IssMarkerFactory.RegistrySection.PROPERTY_VALUENAME, IssMarkerFactory.RegistrySection.PROPERTY_VALUENAME_VALUE,
            "registry.property.valuename", IssValueType.String),
    ValueData("ValueData", IssMarkerFactory.RegistrySection.PROPERTY_VALUEDATA, IssMarkerFactory.RegistrySection.PROPERTY_VALUEDATA_VALUE,
            "registry.property.valuedata", IssValueType.String),
    Permissions("Permissions", IssMarkerFactory.RegistrySection.PROPERTY_PERMISSIONS, IssMarkerFactory.RegistrySection.PROPERTY_PERMISSIONS_VALUE,
            "registry.property.valuetype", IssValueType.DirectMultiple),
    Flags("Flags", IssMarkerFactory.RegistrySection.PROPERTY_FLAGS, IssMarkerFactory.RegistrySection.PROPERTY_FLAGS_VALUE,
            "registry.property.flags", IssValueType.DirectMultiple),
    //Commons
    ComponentReference(IssCommonProperty.ComponentReference),
    TaskReference(IssCommonProperty.TaskReference),
    Languages(IssCommonProperty.Languages),
    MinimalVersion(IssCommonProperty.MinimalVersion),
    OnlyBelowVersion(IssCommonProperty.OnlyBelowVersion);

    public static IssRegistryProperty fromId(final String id) {
        return IssPropertyIdentifier.fromId(id, IssRegistryProperty.class);
    }

    public static IElementType getPropertyMarkerElementFromId(final String id) {
        return IssPropertyIdentifier.getItemMarkerElementFromId(id, IssRegistryProperty.class);
    }

    public static IElementType getPropertyValueMarkerElementFromId(final String id) {
        return IssPropertyIdentifier.getSingleValueMarkerElementFromId(id, IssRegistryProperty.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated, required;
    private final IElementType itemMarkerElement, propertyValueMarkerElement;
    private final IssValueType valueType;

    private IssRegistryProperty(final IssPropertyIdentifier sectionIdentifier) {
        this(sectionIdentifier.getId(), sectionIdentifier.getPropertyMarkerElement(), sectionIdentifier.getPropertyValueMarkerElement(),
                sectionIdentifier.getDescriptionKey(), sectionIdentifier.getValueType(), sectionIdentifier.isRequired(),
                sectionIdentifier.isDeprecated());
    }

    private IssRegistryProperty(String id, IElementType itemMarkerElement, IElementType propertyValueMarkerElement, String descriptionKey,
                                IssValueType valueType) {
        this(id, itemMarkerElement, propertyValueMarkerElement, descriptionKey, valueType, false);
    }

    private IssRegistryProperty(String id, IElementType itemMarkerElement, IElementType propertyValueMarkerElement, String descriptionKey,
                                IssValueType valueType, boolean required) {
        this(id, itemMarkerElement, propertyValueMarkerElement, descriptionKey, valueType, required, false);
    }

    private IssRegistryProperty(String id, IElementType itemMarkerElement, IElementType propertyValueMarkerElement, String descriptionKey,
                                IssValueType valueType, boolean required, boolean deprecated) {
        this.id = id;
        this.deprecated = deprecated;
        this.required = required;
        this.itemMarkerElement = itemMarkerElement;
        this.propertyValueMarkerElement = propertyValueMarkerElement;
        this.descriptionKey = descriptionKey;
        this.valueType = valueType;
    }

    @NotNull
    @Override
    public String getId() {
        return id;
    }

    @NotNull
    @Override
    public String getDescriptionKey() {
        return descriptionKey;
    }


    @NotNull
    @Override
    public IElementType getPropertyMarkerElement() {
        return itemMarkerElement;
    }

    @Override
    public IElementType getPropertyValueMarkerElement() {
        return propertyValueMarkerElement;
    }

    @Override
    public boolean isDeprecated() {
        return deprecated;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @NotNull
    @Override
    public IssValueType getValueType() {
        return valueType;
    }


    @Override
    public String toString() {
        return id;
    }
}
