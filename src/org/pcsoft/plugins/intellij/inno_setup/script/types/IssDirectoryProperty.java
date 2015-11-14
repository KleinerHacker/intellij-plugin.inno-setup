package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;

/**
 * Created by Christoph on 23.12.2014.
 */
public enum IssDirectoryProperty implements IssDefinableSectionIdentifier {
    Name("Name", IssMarkerFactory.DirectorySection.PROPERTY_NAME, IssMarkerFactory.DirectorySection.PROPERTY_NAME_VALUE,
            "directory.property.name", IssValueType.String, true),
    Attributes("Attribs", IssMarkerFactory.DirectorySection.PROPERTY_ATTRIBUTE, IssMarkerFactory.DirectorySection.PROPERTY_ATTRIBUTE_VALUE,
            "directory.property.attribs", IssValueType.DirectMultiple),
    Permissions("Permissions", IssMarkerFactory.DirectorySection.PROPERTY_PERMISSIONS, IssMarkerFactory.DirectorySection.PROPERTY_PERMISSIONS_VALUE,
            "directory.property.permissions", IssValueType.DirectMultiple),
    Flags("Flags", IssMarkerFactory.DirectorySection.PROPERTY_FLAGS, IssMarkerFactory.DirectorySection.PROPERTY_FLAGS_VALUE,
            "directory.property.flags", IssValueType.DirectMultiple),
    ComponentReference("Components", IssMarkerFactory.DirectorySection.PROPERTY_COMPONENTS, IssMarkerFactory.DirectorySection.PROPERTY_COMPONENTS_VALUE,
            "common.property.components", IssValueType.DirectMultiple),
    TaskReference("Tasks", IssMarkerFactory.DirectorySection.PROPERTY_TASKS, IssMarkerFactory.DirectorySection.PROPERTY_TASKS_VALUE,
            "common.property.tasks", IssValueType.DirectMultiple),
    //Commons
    Languages(IssCommonProperty.Languages),
    MinimalVersion(IssCommonProperty.MinimalVersion),
    OnlyBelowVersion(IssCommonProperty.OnlyBelowVersion);

    public static IssDirectoryProperty fromId(final String id) {
        return IssSectionIdentifier.fromId(id, IssDirectoryProperty.class);
    }

    public static IElementType getItemMarkerElementFromId(final String id) {
        return IssSectionIdentifier.getItemMarkerElementFromId(id, IssDirectoryProperty.class);
    }

    public static IElementType getValueMarkerElementFromId(final String id) {
        return IssDefinableSectionIdentifier.getSingleValueMarkerElementFromId(id, IssDirectoryProperty.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated, required;
    private final IElementType itemMarkerElement, valueMarkerElement;
    private final IssValueType valueType;

    private IssDirectoryProperty(final IssDefinableSectionIdentifier sectionIdentifier) {
        this(sectionIdentifier.getId(), sectionIdentifier.getItemMarkerElement(), sectionIdentifier.getValueMarkerElement(),
                sectionIdentifier.getDescriptionKey(), sectionIdentifier.getValueType(), sectionIdentifier.isRequired(),
                sectionIdentifier.isDeprecated());
    }

    private IssDirectoryProperty(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey,
                                 IssValueType valueType) {
        this(id, itemMarkerElement, valueMarkerElement, descriptionKey, valueType, false);
    }

    private IssDirectoryProperty(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey,
                                 IssValueType valueType, boolean required) {
        this(id, itemMarkerElement, valueMarkerElement, descriptionKey, valueType, required, false);
    }

    private IssDirectoryProperty(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey,
                                 IssValueType valueType, boolean required, boolean deprecated) {
        this.id = id;
        this.deprecated = deprecated;
        this.required = required;
        this.itemMarkerElement = itemMarkerElement;
        this.valueMarkerElement = valueMarkerElement;
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
    public IElementType getItemMarkerElement() {
        return itemMarkerElement;
    }


    @Override
    public IElementType getValueMarkerElement() {
        return valueMarkerElement;
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
