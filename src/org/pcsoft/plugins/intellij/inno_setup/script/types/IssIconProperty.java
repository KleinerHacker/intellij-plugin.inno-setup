package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;

/**
 * Created by Christoph on 23.12.2014.
 */
public enum IssIconProperty implements IssDefinableSectionIdentifier {
    Name("Name", IssMarkerFactory.IconSection.ITEM_NAME, IssMarkerFactory.IconSection.ITEM_NAME_VALUE,
            "icon.property.name", IssValueType.String, true),
    Filename("Filename", IssMarkerFactory.IconSection.ITEM_FILENAME, IssMarkerFactory.IconSection.ITEM_FILENAME_VALUE,
            "icon.property.filename", IssValueType.String, true),
    Parameters("Parameters", IssMarkerFactory.IconSection.ITEM_PARAMETERS, IssMarkerFactory.IconSection.ITEM_PARAMETERS_VALUE,
            "icon.property.parameters", IssValueType.String),
    WorkingDirectory("WorkingDir", IssMarkerFactory.IconSection.ITEM_WORKINGDIR, IssMarkerFactory.IconSection.ITEM_WORKINGDIR_VALUE,
            "icon.property.workingdir", IssValueType.String),
    HotKey("HotKey", IssMarkerFactory.IconSection.ITEM_HOTKEY, IssMarkerFactory.IconSection.ITEM_HOTKEY_VALUE,
            "icon.property.hotkey", IssValueType.String),
    Comment("Comment", IssMarkerFactory.IconSection.ITEM_COMMENT, IssMarkerFactory.IconSection.ITEM_COMMENT_VALUE,
            "icon.property.comment", IssValueType.String),
    IconFilename("IconFilename", IssMarkerFactory.IconSection.ITEM_ICONFILENAME, IssMarkerFactory.IconSection.ITEM_ICONFILENAME_VALUE,
            "icon.property.iconfilename", IssValueType.String),
    IconIndex("IconIndex", IssMarkerFactory.IconSection.ITEM_ICONINDEX, IssMarkerFactory.IconSection.ITEM_ICONINDEX_VALUE,
            "icon.property.iconindex", IssValueType.Integer),
    AppUserModelID("AppUserModelID", IssMarkerFactory.IconSection.ITEM_APPUSERMODELID, IssMarkerFactory.IconSection.ITEM_APPUSERMODELID_VALUE,
            "icon.property.appusermodelid", IssValueType.String),
    Flags("Flags", IssMarkerFactory.IconSection.ITEM_FLAGS, IssMarkerFactory.IconSection.ITEM_FLAGS_VALUE,
            "icon.property.flags", IssValueType.DirectMultiple),
    ComponentReference("Components", IssMarkerFactory.IconSection.ITEM_COMPONENTS, IssMarkerFactory.IconSection.ITEM_COMPONENTS_VALUE,
            "common.property.components", IssValueType.DirectMultiple),
    TaskReference("Tasks", IssMarkerFactory.IconSection.ITEM_TASKS, IssMarkerFactory.IconSection.ITEM_TASKS_VALUE,
            "common.property.tasks", IssValueType.DirectMultiple),
    //Commons
    Languages(IssCommonProperty.Languages),
    MinimalVersion(IssCommonProperty.MinimalVersion),
    OnlyBelowVersion(IssCommonProperty.OnlyBelowVersion);

    public static IssIconProperty fromId(final String id) {
        return IssSectionIdentifier.fromId(id, IssIconProperty.class);
    }

    public static IElementType getItemMarkerElementFromId(final String id) {
        return IssSectionIdentifier.getItemMarkerElementFromId(id, IssIconProperty.class);
    }

    public static IElementType getValueMarkerElementFromId(final String id) {
        return IssDefinableSectionIdentifier.getSingleValueMarkerElementFromId(id, IssIconProperty.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated, required;
    private final IElementType itemMarkerElement, valueMarkerElement;
    private final IssValueType valueType;

    private IssIconProperty(final IssDefinableSectionIdentifier sectionIdentifier) {
        this(sectionIdentifier.getId(), sectionIdentifier.getItemMarkerElement(), sectionIdentifier.getValueMarkerElement(),
                sectionIdentifier.getDescriptionKey(), sectionIdentifier.getValueType(), sectionIdentifier.isRequired(),
                sectionIdentifier.isDeprecated());
    }

    private IssIconProperty(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey,
                            IssValueType valueType) {
        this(id, itemMarkerElement, valueMarkerElement, descriptionKey, valueType, false);
    }

    private IssIconProperty(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey,
                            IssValueType valueType, boolean required) {
        this(id, itemMarkerElement, valueMarkerElement, descriptionKey, valueType, required, false);
    }

    private IssIconProperty(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey,
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
    public boolean isRequired() {
        return required;
    }

    @Override
    public boolean isDeprecated() {
        return deprecated;
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
