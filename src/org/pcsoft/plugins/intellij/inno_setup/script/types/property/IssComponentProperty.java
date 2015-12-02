package org.pcsoft.plugins.intellij.inno_setup.script.types.property;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssValueType;

/**
 * Created by Christoph on 28.12.2014.
 */
public enum IssComponentProperty implements IssPropertyIdentifier {
    Name("Name", IssMarkerFactory.ComponentSection.PROPERTY_NAME, IssMarkerFactory.ComponentSection.PROPERTY_NAME_VALUE,
            "component.property.name", IssValueType.DirectSingle, true),
    Description("Description", IssMarkerFactory.ComponentSection.PROPERTY_DESCRIPTION, IssMarkerFactory.ComponentSection.PROPERTY_DESCRIPTION_VALUE,
            "component.property.description", IssValueType.String, true),
    TypeReference("Types", IssMarkerFactory.ComponentSection.PROPERTY_TYPES, IssMarkerFactory.ComponentSection.PROPERTY_TYPES_VALUE,
            "component.property.types", IssValueType.DirectMultiple),
    ExtraDiskSpaceRequired("ExtraDiskSpaceRequired", IssMarkerFactory.ComponentSection.PROPERTY_EXTRADISKSPACEREQUIRED, IssMarkerFactory.ComponentSection.PROPERTY_EXTRADISKSPACEREQUIRED_VALUE,
            "component.property.extra_disk_space_required", IssValueType.Integer),
    Flags("Flags", IssMarkerFactory.ComponentSection.PROPERTY_FLAGS, IssMarkerFactory.ComponentSection.PROPERTY_FLAGS_VALUE,
            "component.property.flags", IssValueType.DirectMultiple),
    //Commons
    Languages(IssCommonProperty.Languages),
    MinimalVersion(IssCommonProperty.MinimalVersion),
    OnlyBelowVersion(IssCommonProperty.OnlyBelowVersion);

    public static IssComponentProperty fromId(final String id) {
        return IssPropertyIdentifier.fromId(id, IssComponentProperty.class);
    }

    public static IElementType getPropertyMarkerElementFromId(final String id) {
        return IssPropertyIdentifier.getItemMarkerElementFromId(id, IssComponentProperty.class);
    }

    public static IElementType getPropertyValueMarkerElementFromId(final String id) {
        return IssPropertyIdentifier.getSingleValueMarkerElementFromId(id, IssComponentProperty.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated, required;
    private final IElementType itemMarkerElement, propertyValueMarkerElement;
    private final IssValueType valueType;

    private IssComponentProperty(final IssPropertyIdentifier sectionIdentifier) {
        this(sectionIdentifier.getId(), sectionIdentifier.getPropertyMarkerElement(), sectionIdentifier.getPropertyValueMarkerElement(),
                sectionIdentifier.getDescriptionKey(), sectionIdentifier.getValueType(), sectionIdentifier.isRequired(),
                sectionIdentifier.isDeprecated());
    }

    private IssComponentProperty(String id, IElementType itemMarkerElement, IElementType propertyValueMarkerElement, String descriptionKey,
                                 IssValueType valueType) {
        this(id, itemMarkerElement, propertyValueMarkerElement, descriptionKey, valueType, false);
    }

    private IssComponentProperty(String id, IElementType itemMarkerElement, IElementType propertyValueMarkerElement, String descriptionKey,
                                 IssValueType valueType, boolean required) {
        this(id, itemMarkerElement, propertyValueMarkerElement, descriptionKey, valueType, required, false);
    }

    private IssComponentProperty(String id, IElementType itemMarkerElement, IElementType propertyValueMarkerElement, String descriptionKey,
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
