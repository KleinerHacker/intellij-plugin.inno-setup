package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;

/**
 * Created by Christoph on 23.12.2014.
 */
public enum IssFileProperty implements IssDefinableSectionIdentifier {
    Source("Source", IssMarkerFactory.FileSection.ITEM_SOURCE, IssMarkerFactory.FileSection.ITEM_SOURCE_VALUE, "property.files.source"),
    DestinationDir("DestDir", IssMarkerFactory.FileSection.ITEM_DESTDIR, IssMarkerFactory.FileSection.ITEM_DESTDIR_VALUE, "property.files.dest_dir"),
    DestinationName("DestName", IssMarkerFactory.ITEM_DEFAULT, null, "property.files.dest_name"),
    Excludes("Excludes", IssMarkerFactory.ITEM_DEFAULT, null, "property.files.excludes"),
    ExternalSize("ExternalSize", IssMarkerFactory.ITEM_DEFAULT, null, "property.files.external_size"),
    CopyMode("CopyMode", IssMarkerFactory.FileSection.ITEM_COPYMODE, IssMarkerFactory.FileSection.ITEM_COPYMODE_VALUE, "property.files.copy_mode"),
    Attributes("Attribs", IssMarkerFactory.FileSection.ITEM_ATTRIBUTE, IssMarkerFactory.FileSection.ITEM_ATTRIBUTE_VALUE, "property.files.attribs"),
    Permissions("Permissions", IssMarkerFactory.FileSection.ITEM_PERMISSIONS, IssMarkerFactory.FileSection.ITEM_PERMISSIONS_VALUE, "property.files.permissions"),
    FontInstall("FontInstall", IssMarkerFactory.ITEM_DEFAULT, null, "property.files.font_install"),
    StrongAssemblyName("StrongAssemblyName", IssMarkerFactory.ITEM_DEFAULT, null, "property.files.strong_assembly_name"),
    Flags("Flags", IssMarkerFactory.FileSection.ITEM_FLAGS, IssMarkerFactory.FileSection.ITEM_FLAGS_VALUE, "property.files.flags"),
    Components("Components", IssMarkerFactory.FileSection.ITEM_COMPONENTS, IssMarkerFactory.FileSection.ITEM_COMPONENTS_VALUE, "property.common.components"),
    Tasks("Tasks", IssMarkerFactory.FileSection.ITEM_TASKS, IssMarkerFactory.FileSection.ITEM_TASKS_VALUE, "property.common.tasks"),
    //Commons
    Languages(IssCommonProperty.Languages),
    MinimalVersion(IssCommonProperty.MinimalVersion),
    OnlyBelowVersion(IssCommonProperty.OnlyBelowVersion);

    public static IssFileProperty fromId(final String id) {
        return IssSectionIdentifier.fromId(id, IssFileProperty.class);
    }

    public static IElementType getItemMarkerElementFromId(final String id) {
        return IssSectionIdentifier.getItemMarkerElementFromId(id, IssFileProperty.class);
    }

    public static IElementType getValueMarkerElementFromId(final String id) {
        return IssDefinableSectionIdentifier.getSingleValueMarkerElementFromId(id, IssFileProperty.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;
    private final IElementType itemMarkerElement, valueMarkerElement;

    private IssFileProperty(final IssDefinableSectionIdentifier sectionIdentifier) {
        this(sectionIdentifier.getId(), sectionIdentifier.getItemMarkerElement(), sectionIdentifier.getValueMarkerElement(), sectionIdentifier.getDescriptionKey(), sectionIdentifier.isDeprecated());
    }

    private IssFileProperty(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey) {
        this(id, itemMarkerElement, valueMarkerElement, descriptionKey, false);
    }

    private IssFileProperty(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey, boolean deprecated) {
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
