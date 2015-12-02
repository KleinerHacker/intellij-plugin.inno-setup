package org.pcsoft.plugins.intellij.inno_setup.script.types.property;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssValueType;

/**
 * Created by Christoph on 23.12.2014.
 */
public enum IssIconProperty implements IssPropertyIdentifier {
    Name("Name", IssMarkerFactory.IconSection.PROPERTY_NAME, IssMarkerFactory.IconSection.PROPERTY_NAME_VALUE,
            "icon.property.name", IssValueType.String, true),
    Filename("Filename", IssMarkerFactory.IconSection.PROPERTY_FILENAME, IssMarkerFactory.IconSection.PROPERTY_FILENAME_VALUE,
            "icon.property.filename", IssValueType.String, true),
    Parameters("Parameters", IssMarkerFactory.IconSection.PROPERTY_PARAMETERS, IssMarkerFactory.IconSection.PROPERTY_PARAMETERS_VALUE,
            "icon.property.parameters", IssValueType.String),
    WorkingDirectory("WorkingDir", IssMarkerFactory.IconSection.PROPERTY_WORKINGDIR, IssMarkerFactory.IconSection.PROPERTY_WORKINGDIR_VALUE,
            "icon.property.workingdir", IssValueType.String),
    HotKey("HotKey", IssMarkerFactory.IconSection.PROPERTY_HOTKEY, IssMarkerFactory.IconSection.PROPERTY_HOTKEY_VALUE,
            "icon.property.hotkey", IssValueType.String),
    Comment("Comment", IssMarkerFactory.IconSection.PROPERTY_COMMENT, IssMarkerFactory.IconSection.PROPERTY_COMMENT_VALUE,
            "icon.property.comment", IssValueType.String),
    IconFilename("IconFilename", IssMarkerFactory.IconSection.PROPERTY_ICONFILENAME, IssMarkerFactory.IconSection.PROPERTY_ICONFILENAME_VALUE,
            "icon.property.iconfilename", IssValueType.String),
    IconIndex("IconIndex", IssMarkerFactory.IconSection.PROPERTY_ICONINDEX, IssMarkerFactory.IconSection.PROPERTY_ICONINDEX_VALUE,
            "icon.property.iconindex", IssValueType.Integer),
    AppUserModelID("AppUserModelID", IssMarkerFactory.IconSection.PROPERTY_APPUSERMODELID, IssMarkerFactory.IconSection.PROPERTY_APPUSERMODELID_VALUE,
            "icon.property.appusermodelid", IssValueType.String),
    Flags("Flags", IssMarkerFactory.IconSection.PROPERTY_FLAGS, IssMarkerFactory.IconSection.PROPERTY_FLAGS_VALUE,
            "icon.property.flags", IssValueType.DirectMultiple),
    //Commons
    Languages(IssCommonProperty.Languages),
    ComponentReference(IssCommonProperty.ComponentReference),
    TaskReference(IssCommonProperty.TaskReference),
    MinimalVersion(IssCommonProperty.MinimalVersion),
    OnlyBelowVersion(IssCommonProperty.OnlyBelowVersion);

    public static IssIconProperty fromId(final String id) {
        return IssPropertyIdentifier.fromId(id, IssIconProperty.class);
    }

    public static IElementType getPropertyMarkerElementFromId(final String id) {
        return IssPropertyIdentifier.getItemMarkerElementFromId(id, IssIconProperty.class);
    }

    public static IElementType getPropertyValueMarkerElementFromId(final String id) {
        return IssPropertyIdentifier.getSingleValueMarkerElementFromId(id, IssIconProperty.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated, required;
    private final IElementType itemMarkerElement, propertyValueMarkerElement;
    private final IssValueType valueType;

    private IssIconProperty(final IssPropertyIdentifier sectionIdentifier) {
        this(sectionIdentifier.getId(), sectionIdentifier.getPropertyMarkerElement(), sectionIdentifier.getPropertyValueMarkerElement(),
                sectionIdentifier.getDescriptionKey(), sectionIdentifier.getValueType(), sectionIdentifier.isRequired(),
                sectionIdentifier.isDeprecated());
    }

    private IssIconProperty(String id, IElementType itemMarkerElement, IElementType propertyValueMarkerElement, String descriptionKey,
                            IssValueType valueType) {
        this(id, itemMarkerElement, propertyValueMarkerElement, descriptionKey, valueType, false);
    }

    private IssIconProperty(String id, IElementType itemMarkerElement, IElementType propertyValueMarkerElement, String descriptionKey,
                            IssValueType valueType, boolean required) {
        this(id, itemMarkerElement, propertyValueMarkerElement, descriptionKey, valueType, required, false);
    }

    private IssIconProperty(String id, IElementType itemMarkerElement, IElementType propertyValueMarkerElement, String descriptionKey,
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
