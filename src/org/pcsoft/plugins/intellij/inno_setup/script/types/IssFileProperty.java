package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;

/**
 * Created by Christoph on 23.12.2014.
 */
public enum IssFileProperty implements IssDefinableSectionIdentifier {
    Source("Source", IssMarkerFactory.FileSection.ITEM_SOURCE, IssMarkerFactory.FileSection.ITEM_SOURCE_VALUE,
            "file.property.source", IssValueType.String, true),
    DestinationDirectory("DestDir", IssMarkerFactory.FileSection.ITEM_DESTDIR, IssMarkerFactory.FileSection.ITEM_DESTDIR_VALUE,
            "file.property.dest_dir", IssValueType.String, true),
    DestinationName("DestName", IssMarkerFactory.FileSection.ITEM_DESTNAME, IssMarkerFactory.FileSection.ITEM_DESTNAME_VALUE,
            "file.property.dest_name", IssValueType.String),
    Excludes("Excludes", IssMarkerFactory.FileSection.ITEM_EXCLUDES, IssMarkerFactory.FileSection.ITEM_EXCLUDES_VALUE,
            "file.property.excludes", IssValueType.String),
    ExternalSize("ExternalSize", IssMarkerFactory.FileSection.ITEM_EXTERNALSIZE, IssMarkerFactory.FileSection.ITEM_EXTERNALSIZE_VALUE,
            "file.property.external_size", IssValueType.Integer),
    CopyMode("CopyMode", IssMarkerFactory.FileSection.ITEM_COPYMODE, IssMarkerFactory.FileSection.ITEM_COPYMODE_VALUE,
            "file.property.copy_mode", IssValueType.DirectSingle),
    Attributes("Attribs", IssMarkerFactory.FileSection.ITEM_ATTRIBUTE, IssMarkerFactory.FileSection.ITEM_ATTRIBUTE_VALUE,
            "file.property.attribs", IssValueType.DirectMultiple),
    Permissions("Permissions", IssMarkerFactory.FileSection.ITEM_PERMISSIONS, IssMarkerFactory.FileSection.ITEM_PERMISSIONS_VALUE,
            "file.property.permissions", IssValueType.DirectMultiple),
    FontInstall("FontInstall", IssMarkerFactory.FileSection.ITEM_FONTINSTALL, IssMarkerFactory.FileSection.ITEM_FONTINSTALL_VALUE,
            "file.property.font_install", IssValueType.String),
    StrongAssemblyName("StrongAssemblyName", IssMarkerFactory.FileSection.ITEM_STRONGASSEMBLYNAME, IssMarkerFactory.FileSection.ITEM_STRONGASSEMBLYNAME_VALUE,
            "file.property.strong_assembly_name", IssValueType.String),
    Flags("Flags", IssMarkerFactory.FileSection.ITEM_FLAGS, IssMarkerFactory.FileSection.ITEM_FLAGS_VALUE,
            "file.property.flags", IssValueType.DirectMultiple),
    ComponentReference("Components", IssMarkerFactory.FileSection.ITEM_COMPONENTS, IssMarkerFactory.FileSection.ITEM_COMPONENTS_VALUE,
            "common.property.components", IssValueType.DirectMultiple),
    TaskReference("Tasks", IssMarkerFactory.FileSection.ITEM_TASKS, IssMarkerFactory.FileSection.ITEM_TASKS_VALUE,
            "common.property.tasks", IssValueType.DirectMultiple),
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
    private final boolean deprecated, required;
    private final IElementType itemMarkerElement, valueMarkerElement;
    private final IssValueType valueType;

    private IssFileProperty(final IssDefinableSectionIdentifier sectionIdentifier) {
        this(sectionIdentifier.getId(), sectionIdentifier.getItemMarkerElement(), sectionIdentifier.getValueMarkerElement(),
                sectionIdentifier.getDescriptionKey(), sectionIdentifier.getValueType(), sectionIdentifier.isRequired(),
                sectionIdentifier.isDeprecated());
    }

    private IssFileProperty(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey,
                            IssValueType valueType) {
        this(id, itemMarkerElement, valueMarkerElement, descriptionKey, valueType, false);
    }

    private IssFileProperty(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey,
                            IssValueType valueType, boolean required) {
        this(id, itemMarkerElement, valueMarkerElement, descriptionKey, valueType, required, false);
    }

    private IssFileProperty(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey,
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
