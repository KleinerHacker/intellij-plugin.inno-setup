package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;

/**
 * Created by Christoph on 23.12.2014.
 */
public enum IssRunProperty implements IssDefinableSectionIdentifier {
    Filename("Filename", IssMarkerFactory.RunSection.PROPERTY_FILENAME, IssMarkerFactory.RunSection.PROPERTY_FILENAME_VALUE,
            "run.property.filename", IssValueType.String, true),
    Description("Description", IssMarkerFactory.RunSection.PROPERTY_DESCRIPTION, IssMarkerFactory.RunSection.PROPERTY_DESCRIPTION_VALUE,
            "run.property.description", IssValueType.String),
    Parameters("Parameters", IssMarkerFactory.RunSection.PROPERTY_PARAMETERS, IssMarkerFactory.RunSection.PROPERTY_PARAMETERS_VALUE,
            "run.property.parameters", IssValueType.String),
    WorkingDirectory("WorkingDir", IssMarkerFactory.RunSection.PROPERTY_WORKINGDIR, IssMarkerFactory.RunSection.PROPERTY_WORKINGDIR_VALUE,
            "run.property.workingdir", IssValueType.String),
    StatusMessage("StatusMsg", IssMarkerFactory.RunSection.PROPERTY_STATUSMSG, IssMarkerFactory.RunSection.PROPERTY_STATUSMSG_VALUE,
            "run.property.statusmsg", IssValueType.String),
    RunOnceId("RunOnceId", IssMarkerFactory.RunSection.PROPERTY_RUNONCEID, IssMarkerFactory.RunSection.PROPERTY_RUNONCEID_VALUE,
            "run.property.parameters", IssValueType.String),
    Verb("Verb", IssMarkerFactory.RunSection.PROPERTY_VERB, IssMarkerFactory.RunSection.PROPERTY_VERB_VALUE,
            "run.property.verb", IssValueType.String),
    Flags("Flags", IssMarkerFactory.RunSection.PROPERTY_FLAGS, IssMarkerFactory.RunSection.PROPERTY_FLAGS_VALUE,
            "run.property.flags", IssValueType.DirectMultiple),
    //Commons
    Languages(IssCommonProperty.Languages),
    ComponentReference(IssCommonProperty.ComponentReference),
    TaskReference(IssCommonProperty.TaskReference),
    MinimalVersion(IssCommonProperty.MinimalVersion),
    OnlyBelowVersion(IssCommonProperty.OnlyBelowVersion);

    public static IssRunProperty fromId(final String id) {
        return IssSectionIdentifier.fromId(id, IssRunProperty.class);
    }

    public static IElementType getItemMarkerElementFromId(final String id) {
        return IssSectionIdentifier.getItemMarkerElementFromId(id, IssRunProperty.class);
    }

    public static IElementType getValueMarkerElementFromId(final String id) {
        return IssDefinableSectionIdentifier.getSingleValueMarkerElementFromId(id, IssRunProperty.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated, required;
    private final IElementType itemMarkerElement, valueMarkerElement;
    private final IssValueType valueType;

    private IssRunProperty(final IssDefinableSectionIdentifier sectionIdentifier) {
        this(sectionIdentifier.getId(), sectionIdentifier.getItemMarkerElement(), sectionIdentifier.getValueMarkerElement(),
                sectionIdentifier.getDescriptionKey(), sectionIdentifier.getValueType(), sectionIdentifier.isRequired(),
                sectionIdentifier.isDeprecated());
    }

    private IssRunProperty(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey,
                           IssValueType valueType) {
        this(id, itemMarkerElement, valueMarkerElement, descriptionKey, valueType, false);
    }

    private IssRunProperty(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey,
                           IssValueType valueType, boolean required) {
        this(id, itemMarkerElement, valueMarkerElement, descriptionKey, valueType, required, false);
    }

    private IssRunProperty(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey,
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
