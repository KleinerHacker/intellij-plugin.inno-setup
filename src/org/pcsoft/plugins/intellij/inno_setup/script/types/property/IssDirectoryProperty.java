package org.pcsoft.plugins.intellij.inno_setup.script.types.property;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssValueType;

/**
 * Created by Christoph on 23.12.2014.
 */
public enum IssDirectoryProperty implements IssPropertyIdentifier {
    Name("Name", IssMarkerFactory.DirectorySection.PROPERTY_NAME, IssMarkerFactory.DirectorySection.PROPERTY_NAME_VALUE,
            "directory.property.name", IssValueType.String, true),
    Attributes("Attribs", IssMarkerFactory.DirectorySection.PROPERTY_ATTRIBUTE, IssMarkerFactory.DirectorySection.PROPERTY_ATTRIBUTE_VALUE,
            "directory.property.attribs", IssValueType.DirectMultiple),
    Permissions("Permissions", IssMarkerFactory.DirectorySection.PROPERTY_PERMISSIONS, IssMarkerFactory.DirectorySection.PROPERTY_PERMISSIONS_VALUE,
            "directory.property.permissions", IssValueType.DirectMultiple),
    Flags("Flags", IssMarkerFactory.DirectorySection.PROPERTY_FLAGS, IssMarkerFactory.DirectorySection.PROPERTY_FLAGS_VALUE,
            "directory.property.flags", IssValueType.DirectMultiple),
    //Commons
    Languages(IssCommonProperty.Languages),
    ComponentReference(IssCommonProperty.ComponentReference),
    TaskReference(IssCommonProperty.TaskReference),
    MinimalVersion(IssCommonProperty.MinimalVersion),
    OnlyBelowVersion(IssCommonProperty.OnlyBelowVersion);

    public static IssDirectoryProperty fromId(final String id) {
        return IssPropertyIdentifier.fromId(id, IssDirectoryProperty.class);
    }

    public static IElementType getPropertyMarkerElementFromId(final String id) {
        return IssPropertyIdentifier.getItemMarkerElementFromId(id, IssDirectoryProperty.class);
    }

    public static IElementType getPropertyValueMarkerElementFromId(final String id) {
        return IssPropertyIdentifier.getSingleValueMarkerElementFromId(id, IssDirectoryProperty.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated, required;
    private final IElementType itemMarkerElement, propertyValueMarkerElement;
    private final IssValueType valueType;

    private IssDirectoryProperty(final IssPropertyIdentifier sectionIdentifier) {
        this(sectionIdentifier.getId(), sectionIdentifier.getPropertyMarkerElement(), sectionIdentifier.getPropertyValueMarkerElement(),
                sectionIdentifier.getDescriptionKey(), sectionIdentifier.getValueType(), sectionIdentifier.isRequired(),
                sectionIdentifier.isDeprecated());
    }

    private IssDirectoryProperty(String id, IElementType itemMarkerElement, IElementType propertyValueMarkerElement, String descriptionKey,
                                 IssValueType valueType) {
        this(id, itemMarkerElement, propertyValueMarkerElement, descriptionKey, valueType, false);
    }

    private IssDirectoryProperty(String id, IElementType itemMarkerElement, IElementType propertyValueMarkerElement, String descriptionKey,
                                 IssValueType valueType, boolean required) {
        this(id, itemMarkerElement, propertyValueMarkerElement, descriptionKey, valueType, required, false);
    }

    private IssDirectoryProperty(String id, IElementType itemMarkerElement, IElementType propertyValueMarkerElement, String descriptionKey,
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
