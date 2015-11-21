package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;

/**
 * Created by Christoph on 23.12.2014.
 */
public enum IssUninstallRunProperty implements IssDefinablePropertyIdentifier {
    Filename("Filename", IssMarkerFactory.UninstallRunSection.PROPERTY_FILENAME, IssMarkerFactory.UninstallRunSection.PROPERTY_FILENAME_VALUE,
            "uninstall_run.property.filename", IssValueType.String, true),
    Parameters("Parameters", IssMarkerFactory.UninstallRunSection.PROPERTY_PARAMETERS, IssMarkerFactory.UninstallRunSection.PROPERTY_PARAMETERS_VALUE,
            "uninstall_run.property.parameters", IssValueType.String),
    WorkingDirectory("WorkingDir", IssMarkerFactory.UninstallRunSection.PROPERTY_WORKINGDIR, IssMarkerFactory.UninstallRunSection.PROPERTY_WORKINGDIR_VALUE,
            "uninstall_run.property.workingdir", IssValueType.String),
    RunOnceId("RunOnceId", IssMarkerFactory.UninstallRunSection.PROPERTY_RUNONCEID, IssMarkerFactory.UninstallRunSection.PROPERTY_RUNONCEID_VALUE,
            "uninstall_run.property.runonceid", IssValueType.String),
    Verb("Verb", IssMarkerFactory.UninstallRunSection.PROPERTY_VERB, IssMarkerFactory.UninstallRunSection.PROPERTY_VERB_VALUE,
            "uninstall_run.property.verb", IssValueType.String),
    Flags("Flags", IssMarkerFactory.UninstallRunSection.PROPERTY_FLAGS, IssMarkerFactory.UninstallRunSection.PROPERTY_FLAGS_VALUE,
            "uninstall_run.property.flags", IssValueType.DirectMultiple),
    //Commons
    Languages(IssCommonProperty.Languages),
    ComponentReference(IssCommonProperty.ComponentReference),
    TaskReference(IssCommonProperty.TaskReference),
    MinimalVersion(IssCommonProperty.MinimalVersion),
    OnlyBelowVersion(IssCommonProperty.OnlyBelowVersion);

    public static IssUninstallRunProperty fromId(final String id) {
        return IssPropertyIdentifier.fromId(id, IssUninstallRunProperty.class);
    }

    public static IElementType getPropertyMarkerElementFromId(final String id) {
        return IssPropertyIdentifier.getItemMarkerElementFromId(id, IssUninstallRunProperty.class);
    }

    public static IElementType getPropertyValueMarkerElementFromId(final String id) {
        return IssPropertyIdentifier.getSingleValueMarkerElementFromId(id, IssUninstallRunProperty.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated, required;
    private final IElementType itemMarkerElement, propertyValueMarkerElement;
    private final IssValueType valueType;

    private IssUninstallRunProperty(final IssDefinablePropertyIdentifier sectionIdentifier) {
        this(sectionIdentifier.getId(), sectionIdentifier.getPropertyMarkerElement(), sectionIdentifier.getPropertyValueMarkerElement(),
                sectionIdentifier.getDescriptionKey(), sectionIdentifier.getValueType(), sectionIdentifier.isRequired(),
                sectionIdentifier.isDeprecated());
    }

    private IssUninstallRunProperty(String id, IElementType itemMarkerElement, IElementType propertyValueMarkerElement, String descriptionKey,
                                    IssValueType valueType) {
        this(id, itemMarkerElement, propertyValueMarkerElement, descriptionKey, valueType, false);
    }

    private IssUninstallRunProperty(String id, IElementType itemMarkerElement, IElementType propertyValueMarkerElement, String descriptionKey,
                                    IssValueType valueType, boolean required) {
        this(id, itemMarkerElement, propertyValueMarkerElement, descriptionKey, valueType, required, false);
    }

    private IssUninstallRunProperty(String id, IElementType itemMarkerElement, IElementType propertyValueMarkerElement, String descriptionKey,
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
