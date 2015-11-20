package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;

/**
 * Created by Christoph on 23.12.2014.
 */
public enum IssFileProperty implements IssDefinablePropertyIdentifier {
    Source("Source", IssMarkerFactory.FileSection.PROPERTY_SOURCE, IssMarkerFactory.FileSection.PROPERTY_SOURCE_VALUE,
            "file.property.source", IssValueType.String, true),
    DestinationDirectory("DestDir", IssMarkerFactory.FileSection.PROPERTY_DESTDIR, IssMarkerFactory.FileSection.PROPERTY_DESTDIR_VALUE,
            "file.property.dest_dir", IssValueType.String, true),
    DestinationName("DestName", IssMarkerFactory.FileSection.PROPERTY_DESTNAME, IssMarkerFactory.FileSection.PROPERTY_DESTNAME_VALUE,
            "file.property.dest_name", IssValueType.String),
    Excludes("Excludes", IssMarkerFactory.FileSection.PROPERTY_EXCLUDES, IssMarkerFactory.FileSection.PROPERTY_EXCLUDES_VALUE,
            "file.property.excludes", IssValueType.String),
    ExternalSize("ExternalSize", IssMarkerFactory.FileSection.PROPERTY_EXTERNALSIZE, IssMarkerFactory.FileSection.PROPERTY_EXTERNALSIZE_VALUE,
            "file.property.external_size", IssValueType.Integer),
    CopyMode("CopyMode", IssMarkerFactory.FileSection.PROPERTY_COPYMODE, IssMarkerFactory.FileSection.PROPERTY_COPYMODE_VALUE,
            "file.property.copy_mode", IssValueType.DirectSingle),
    Attributes("Attribs", IssMarkerFactory.FileSection.PROPERTY_ATTRIBUTE, IssMarkerFactory.FileSection.PROPERTY_ATTRIBUTE_VALUE,
            "file.property.attribs", IssValueType.DirectMultiple),
    Permissions("Permissions", IssMarkerFactory.FileSection.PROPERTY_PERMISSIONS, IssMarkerFactory.FileSection.PROPERTY_PERMISSIONS_VALUE,
            "file.property.permissions", IssValueType.DirectMultiple),
    FontInstall("FontInstall", IssMarkerFactory.FileSection.PROPERTY_FONTINSTALL, IssMarkerFactory.FileSection.PROPERTY_FONTINSTALL_VALUE,
            "file.property.font_install", IssValueType.String),
    StrongAssemblyName("StrongAssemblyName", IssMarkerFactory.FileSection.PROPERTY_STRONGASSEMBLYNAME, IssMarkerFactory.FileSection.PROPERTY_STRONGASSEMBLYNAME_VALUE,
            "file.property.strong_assembly_name", IssValueType.String),
    Flags("Flags", IssMarkerFactory.FileSection.PROPERTY_FLAGS, IssMarkerFactory.FileSection.PROPERTY_FLAGS_VALUE,
            "file.property.flags", IssValueType.DirectMultiple),
    //Commons
    Languages(IssCommonProperty.Languages),
    ComponentReference(IssCommonProperty.ComponentReference),
    TaskReference(IssCommonProperty.TaskReference),
    MinimalVersion(IssCommonProperty.MinimalVersion),
    OnlyBelowVersion(IssCommonProperty.OnlyBelowVersion);

    public static IssFileProperty fromId(final String id) {
        return IssPropertyIdentifier.fromId(id, IssFileProperty.class);
    }

    public static IElementType getPropertyMarkerElementFromId(final String id) {
        return IssPropertyIdentifier.getItemMarkerElementFromId(id, IssFileProperty.class);
    }

    public static IElementType getPropertyValueMarkerElementFromId(final String id) {
        return IssPropertyIdentifier.getSingleValueMarkerElementFromId(id, IssFileProperty.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated, required;
    private final IElementType itemMarkerElement, propertyValueMarkerElement;
    private final IssValueType valueType;

    private IssFileProperty(final IssDefinablePropertyIdentifier sectionIdentifier) {
        this(sectionIdentifier.getId(), sectionIdentifier.getPropertyMarkerElement(), sectionIdentifier.getPropertyValueMarkerElement(),
                sectionIdentifier.getDescriptionKey(), sectionIdentifier.getValueType(), sectionIdentifier.isRequired(),
                sectionIdentifier.isDeprecated());
    }

    private IssFileProperty(String id, IElementType itemMarkerElement, IElementType propertyValueMarkerElement, String descriptionKey,
                            IssValueType valueType) {
        this(id, itemMarkerElement, propertyValueMarkerElement, descriptionKey, valueType, false);
    }

    private IssFileProperty(String id, IElementType itemMarkerElement, IElementType propertyValueMarkerElement, String descriptionKey,
                            IssValueType valueType, boolean required) {
        this(id, itemMarkerElement, propertyValueMarkerElement, descriptionKey, valueType, required, false);
    }

    private IssFileProperty(String id, IElementType itemMarkerElement, IElementType propertyValueMarkerElement, String descriptionKey,
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
