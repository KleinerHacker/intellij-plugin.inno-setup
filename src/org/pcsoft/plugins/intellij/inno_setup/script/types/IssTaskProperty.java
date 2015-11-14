package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;

/**
 * Created by Christoph on 22.12.2014.
 */
public enum IssTaskProperty implements IssDefinableSectionIdentifier {
    Name("Name", IssMarkerFactory.TaskSection.PROPERTY_NAME, IssMarkerFactory.TaskSection.PROPERTY_NAME_VALUE,
            "task.property.name", IssValueType.DirectSingle, true),
    Description("Description", IssMarkerFactory.TaskSection.PROPERTY_DESCRIPTION, IssMarkerFactory.TaskSection.PROPERTY_DESCRIPTION_VALUE,
            "task.property.description", IssValueType.String, true),
    GroupDescription("GroupDescription", IssMarkerFactory.TaskSection.PROPERTY_GROUPDESCRIPTION, IssMarkerFactory.TaskSection.PROPERTY_GROUPDESCRIPTION_VALUE,
            "task.property.group_description", IssValueType.String),
    Flags("Flags", IssMarkerFactory.TaskSection.PROPERTY_FLAGS, IssMarkerFactory.TaskSection.PROPERTY_FLAGS_VALUE,
            "task.property.flags", IssValueType.DirectMultiple),
    //Commons
    Languages(IssCommonProperty.Languages),
    ComponentReference(IssCommonProperty.ComponentReference),
    MinimalVersion(IssCommonProperty.MinimalVersion),
    OnlyBelowVersion(IssCommonProperty.OnlyBelowVersion);

    public static IssTaskProperty fromId(final String id) {
        return IssSectionIdentifier.fromId(id, IssTaskProperty.class);
    }

    public static IElementType getItemMarkerElementFromId(final String id) {
        return IssSectionIdentifier.getItemMarkerElementFromId(id, IssTaskProperty.class);
    }

    public static IElementType getValueMarkerElementFromId(final String id) {
        return IssDefinableSectionIdentifier.getSingleValueMarkerElementFromId(id, IssTaskProperty.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated, required;
    private final IElementType itemMarkerElement, valueMarkerElement;
    private final IssValueType valueType;

    private IssTaskProperty(final IssDefinableSectionIdentifier sectionIdentifier) {
        this(sectionIdentifier.getId(), sectionIdentifier.getItemMarkerElement(), sectionIdentifier.getValueMarkerElement(),
                sectionIdentifier.getDescriptionKey(), sectionIdentifier.getValueType(), sectionIdentifier.isRequired(),
                sectionIdentifier.isDeprecated());
    }

    private IssTaskProperty(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey,
                            IssValueType valueType) {
        this(id, itemMarkerElement, valueMarkerElement, descriptionKey, valueType, false);
    }

    private IssTaskProperty(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey,
                            IssValueType valueType, boolean required) {
        this(id, itemMarkerElement, valueMarkerElement, descriptionKey, valueType, required, false);
    }

    private IssTaskProperty(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey,
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
