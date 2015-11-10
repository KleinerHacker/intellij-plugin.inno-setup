package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;

/**
 * Created by Christoph on 28.12.2014.
 */
public enum IssComponentProperty implements IssDefinableSectionIdentifier {
    Name("Name", IssMarkerFactory.ComponentSection.ITEM_NAME, IssMarkerFactory.ComponentSection.ITEM_NAME_VALUE,
            "component.property.name", IssValueType.DirectSingle, true),
    Description("Description", IssMarkerFactory.ComponentSection.ITEM_DESCRIPTION, IssMarkerFactory.ComponentSection.ITEM_DESCRIPTION_VALUE,
            "component.property.description", IssValueType.String, true),
    Types("Types", IssMarkerFactory.ComponentSection.ITEM_TYPES, IssMarkerFactory.ComponentSection.ITEM_TYPES_VALUE,
            "component.property.types", IssValueType.DirectMultiple),
    ExtraDiskSpaceRequired("ExtraDiskSpaceRequired", IssMarkerFactory.ComponentSection.ITEM_EXTRADISKSPACEREQUIRED, IssMarkerFactory.ComponentSection.ITEM_EXTRADISKSPACEREQUIRED_VALUE,
            "component.property.extra_disk_space_required", IssValueType.Integer),
    Flags("Flags", IssMarkerFactory.ComponentSection.ITEM_FLAGS, IssMarkerFactory.ComponentSection.ITEM_FLAGS_VALUE,
            "component.property.flags", IssValueType.DirectMultiple),
    //Commons
    Languages(IssCommonProperty.Languages),
    MinimalVersion(IssCommonProperty.MinimalVersion),
    OnlyBelowVersion(IssCommonProperty.OnlyBelowVersion);

    public static IssComponentProperty fromId(final String id) {
        return IssSectionIdentifier.fromId(id, IssComponentProperty.class);
    }

    public static IElementType getItemMarkerElementFromId(final String id) {
        return IssSectionIdentifier.getItemMarkerElementFromId(id, IssComponentProperty.class);
    }

    public static IElementType getValueMarkerElementFromId(final String id) {
        return IssDefinableSectionIdentifier.getSingleValueMarkerElementFromId(id, IssComponentProperty.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated, required;
    private final IElementType itemMarkerElement, valueMarkerElement;
    private final IssValueType valueType;

    private IssComponentProperty(final IssDefinableSectionIdentifier sectionIdentifier) {
        this(sectionIdentifier.getId(), sectionIdentifier.getItemMarkerElement(), sectionIdentifier.getValueMarkerElement(),
                sectionIdentifier.getDescriptionKey(), sectionIdentifier.getValueType(), sectionIdentifier.isRequired(),
                sectionIdentifier.isDeprecated());
    }

    private IssComponentProperty(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey,
                                 IssValueType valueType) {
        this(id, itemMarkerElement, valueMarkerElement, descriptionKey, valueType, false);
    }

    private IssComponentProperty(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey,
                                 IssValueType valueType, boolean required) {
        this(id, itemMarkerElement, valueMarkerElement, descriptionKey, valueType, required, false);
    }

    private IssComponentProperty(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey,
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
