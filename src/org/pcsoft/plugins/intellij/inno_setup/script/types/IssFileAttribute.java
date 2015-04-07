package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;

/**
 * Created by Christoph on 23.12.2014.
 */
public enum IssFileAttribute implements IssDefinableSectionIdentifier {
    Source("Source", IssMarkerFactory.FileSection.ITEM_SOURCE, IssMarkerFactory.FileSection.ITEM_SOURCE_VALUE, "attribute.files.source"),
    DestinationDir("DestDir", IssMarkerFactory.FileSection.ITEM_DESTDIR, IssMarkerFactory.FileSection.ITEM_DESTDIR_VALUE, "attribute.files.dest_dir"),
    DestinationName("DestName", IssMarkerFactory.ITEM_DEFAULT, null, "attribute.files.dest_name"),
    Excludes("Excludes", IssMarkerFactory.ITEM_DEFAULT, null, "attribute.files.excludes"),
    ExternalSize("ExternalSize", IssMarkerFactory.ITEM_DEFAULT, null, "attribute.files.external_size"),
    CopyMode("CopyMode", IssMarkerFactory.ITEM_DEFAULT, null, "attribute.files.copy_mode"),
    Attributes("Attribs", IssMarkerFactory.ITEM_DEFAULT, null, "attribute.files.attribs"),
    Permissions("Permissions", IssMarkerFactory.ITEM_DEFAULT, null, "attribute.files.permissions"),
    FontInstall("FontInstall", IssMarkerFactory.ITEM_DEFAULT, null, "attribute.files.font_install"),
    StrongAssemblyName("StrongAssemblyName", IssMarkerFactory.ITEM_DEFAULT, null, "attribute.files.strong_assembly_name"),
    Flags("Flags", IssMarkerFactory.FileSection.ITEM_FLAGS, IssMarkerFactory.FileSection.ITEM_FLAGS_VALUE, "attribute.files.flags"),
    Components("Components", IssMarkerFactory.FileSection.ITEM_COMPONENTS, IssMarkerFactory.FileSection.ITEM_COMPONENTS_VALUE, "attribute.common.components"),
    Tasks("Tasks", IssMarkerFactory.FileSection.ITEM_TASKS, IssMarkerFactory.FileSection.ITEM_TASKS_VALUE, "attribute.common.tasks"),
    //Commons
    Languages(IssCommonAttribute.Languages),
    MinimalVersion(IssCommonAttribute.MinimalVersion),
    OnlyBelowVersion(IssCommonAttribute.OnlyBelowVersion);

    public static IssFileAttribute fromId(final String id) {
        return IssSectionIdentifier.fromId(id, IssFileAttribute.class);
    }

    public static IElementType getItemMarkerElementFromId(final String id) {
        return IssSectionIdentifier.getItemMarkerElementFromId(id, IssFileAttribute.class);
    }

    public static IElementType getValueMarkerElementFromId(final String id) {
        return IssDefinableSectionIdentifier.getSingleValueMarkerElementFromId(id, IssFileAttribute.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;
    private final IElementType itemMarkerElement, valueMarkerElement;

    private IssFileAttribute(final IssDefinableSectionIdentifier sectionIdentifier) {
        this(sectionIdentifier.getId(), sectionIdentifier.getItemMarkerElement(), sectionIdentifier.getValueMarkerElement(), sectionIdentifier.getDescriptionKey(), sectionIdentifier.isDeprecated());
    }

    private IssFileAttribute(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey) {
        this(id, itemMarkerElement, valueMarkerElement, descriptionKey, false);
    }

    private IssFileAttribute(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey, boolean deprecated) {
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
