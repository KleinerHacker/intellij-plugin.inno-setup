package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;

/**
 * Created by Christoph on 23.12.2014.
 */
public enum IssDirectoryProperty implements IssDefinableSectionIdentifier {
    Name("Name", IssMarkerFactory.DirectorySection.ITEM_NAME, IssMarkerFactory.DirectorySection.ITEM_NAME_VALUE, "directory.property.name"),
    Attributes("Attribs", IssMarkerFactory.DirectorySection.ITEM_ATTRIBUTE, IssMarkerFactory.DirectorySection.ITEM_ATTRIBUTE_VALUE, "directory.property.attribs"),
    Permissions("Permissions", IssMarkerFactory.DirectorySection.ITEM_PERMISSIONS, IssMarkerFactory.DirectorySection.ITEM_PERMISSIONS_VALUE, "directory.property.permissions"),
    Flags("Flags", IssMarkerFactory.DirectorySection.ITEM_FLAGS, IssMarkerFactory.DirectorySection.ITEM_FLAGS_VALUE, "directory.property.flags"),
    Components("Components", IssMarkerFactory.DirectorySection.ITEM_COMPONENTS, IssMarkerFactory.DirectorySection.ITEM_COMPONENTS_VALUE, "common.property.components"),
    Tasks("Tasks", IssMarkerFactory.DirectorySection.ITEM_TASKS, IssMarkerFactory.DirectorySection.ITEM_TASKS_VALUE, "common.property.tasks"),
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
    private final boolean deprecated;
    private final IElementType itemMarkerElement, valueMarkerElement;

    private IssDirectoryProperty(final IssDefinableSectionIdentifier sectionIdentifier) {
        this(sectionIdentifier.getId(), sectionIdentifier.getItemMarkerElement(), sectionIdentifier.getValueMarkerElement(), sectionIdentifier.getDescriptionKey(), sectionIdentifier.isDeprecated());
    }

    private IssDirectoryProperty(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey) {
        this(id, itemMarkerElement, valueMarkerElement, descriptionKey, false);
    }

    private IssDirectoryProperty(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey, boolean deprecated) {
        this.id = id;
        this.deprecated = deprecated;
        this.itemMarkerElement = itemMarkerElement;
        this.valueMarkerElement = valueMarkerElement;
        this.descriptionKey = descriptionKey;
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


}
