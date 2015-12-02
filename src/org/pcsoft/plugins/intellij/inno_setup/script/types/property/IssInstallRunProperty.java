package org.pcsoft.plugins.intellij.inno_setup.script.types.property;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssValueType;

/**
 * Created by Christoph on 23.12.2014.
 */
public enum IssInstallRunProperty implements IssPropertyIdentifier {
    Filename("Filename", IssMarkerFactory.InstallRunSection.PROPERTY_FILENAME, IssMarkerFactory.InstallRunSection.PROPERTY_FILENAME_VALUE,
            "install_run.property.filename", IssValueType.String, true),
    Description("Description", IssMarkerFactory.InstallRunSection.PROPERTY_DESCRIPTION, IssMarkerFactory.InstallRunSection.PROPERTY_DESCRIPTION_VALUE,
            "install_run.property.description", IssValueType.String),
    Parameters("Parameters", IssMarkerFactory.InstallRunSection.PROPERTY_PARAMETERS, IssMarkerFactory.InstallRunSection.PROPERTY_PARAMETERS_VALUE,
            "install_run.property.parameters", IssValueType.String),
    WorkingDirectory("WorkingDir", IssMarkerFactory.InstallRunSection.PROPERTY_WORKINGDIR, IssMarkerFactory.InstallRunSection.PROPERTY_WORKINGDIR_VALUE,
            "install_run.property.workingdir", IssValueType.String),
    StatusMessage("StatusMsg", IssMarkerFactory.InstallRunSection.PROPERTY_STATUSMSG, IssMarkerFactory.InstallRunSection.PROPERTY_STATUSMSG_VALUE,
            "install_run.property.statusmsg", IssValueType.String),
    Verb("Verb", IssMarkerFactory.InstallRunSection.PROPERTY_VERB, IssMarkerFactory.InstallRunSection.PROPERTY_VERB_VALUE,
            "install_run.property.verb", IssValueType.String),
    Flags("Flags", IssMarkerFactory.InstallRunSection.PROPERTY_FLAGS, IssMarkerFactory.InstallRunSection.PROPERTY_FLAGS_VALUE,
            "install_run.property.flags", IssValueType.DirectMultiple),
    //Commons
    Languages(IssCommonProperty.Languages),
    ComponentReference(IssCommonProperty.ComponentReference),
    TaskReference(IssCommonProperty.TaskReference),
    MinimalVersion(IssCommonProperty.MinimalVersion),
    OnlyBelowVersion(IssCommonProperty.OnlyBelowVersion);

    public static IssInstallRunProperty fromId(final String id) {
        return IssPropertyIdentifier.fromId(id, IssInstallRunProperty.class);
    }

    public static IElementType getPropertyMarkerElementFromId(final String id) {
        return IssPropertyIdentifier.getItemMarkerElementFromId(id, IssInstallRunProperty.class);
    }

    public static IElementType getPropertyValueMarkerElementFromId(final String id) {
        return IssPropertyIdentifier.getSingleValueMarkerElementFromId(id, IssInstallRunProperty.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated, required;
    private final IElementType itemMarkerElement, propertyValueMarkerElement;
    private final IssValueType valueType;

    private IssInstallRunProperty(final IssPropertyIdentifier sectionIdentifier) {
        this(sectionIdentifier.getId(), sectionIdentifier.getPropertyMarkerElement(), sectionIdentifier.getPropertyValueMarkerElement(),
                sectionIdentifier.getDescriptionKey(), sectionIdentifier.getValueType(), sectionIdentifier.isRequired(),
                sectionIdentifier.isDeprecated());
    }

    private IssInstallRunProperty(String id, IElementType itemMarkerElement, IElementType propertyValueMarkerElement, String descriptionKey,
                                  IssValueType valueType) {
        this(id, itemMarkerElement, propertyValueMarkerElement, descriptionKey, valueType, false);
    }

    private IssInstallRunProperty(String id, IElementType itemMarkerElement, IElementType propertyValueMarkerElement, String descriptionKey,
                                  IssValueType valueType, boolean required) {
        this(id, itemMarkerElement, propertyValueMarkerElement, descriptionKey, valueType, required, false);
    }

    private IssInstallRunProperty(String id, IElementType itemMarkerElement, IElementType propertyValueMarkerElement, String descriptionKey,
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
