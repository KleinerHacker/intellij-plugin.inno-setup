package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;

/**
 * Created by Christoph on 28.12.2014.
 */
public enum IssComponentAttribute implements IssDefinableSectionIdentifier {
    Name("Name", IssMarkerFactory.ComponentSection.ITEM_NAME, IssMarkerFactory.ComponentSection.ITEM_NAME_VALUE, "attribute.components.name"),
    Description("Description", IssMarkerFactory.ComponentSection.ITEM_DESCRIPTION, IssMarkerFactory.ComponentSection.ITEM_DESCRIPTION_VALUE, "attribute.components.description"),
    Types("Types", IssMarkerFactory.ComponentSection.ITEM_TYPES, IssMarkerFactory.ComponentSection.ITEM_TYPES_VALUE, "attribute.components.types"),
    ExtraDiskSpaceRequired("ExtraDiskSpaceRequired", IssMarkerFactory.ITEM_DEFAULT, null, "attribute.components.extra_disk_space_required"),
    Flags("Flags", IssMarkerFactory.ComponentSection.ITEM_FLAGS, IssMarkerFactory.ComponentSection.ITEM_FLAGS_VALUE, "attribute.components.flags"),
    //Commons
    Languages(IssCommonAttribute.Languages),
    MinimalVersion(IssCommonAttribute.MinimalVersion),
    OnlyBelowVersion(IssCommonAttribute.OnlyBelowVersion);

    public static IssComponentAttribute fromId(final String id) {
        return IssSectionIdentifier.fromId(id, IssComponentAttribute.class);
    }

    public static IElementType getItemMarkerElementFromId(final String id) {
        return IssSectionIdentifier.getItemMarkerElementFromId(id, IssComponentAttribute.class);
    }

    public static IElementType getValueMarkerElementFromId(final String id) {
        return IssDefinableSectionIdentifier.getSingleValueMarkerElementFromId(id, IssComponentAttribute.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;
    private final IElementType itemMarkerElement, valueMarkerElement;

    private IssComponentAttribute(final IssDefinableSectionIdentifier sectionIdentifier) {
        this(sectionIdentifier.getId(), sectionIdentifier.getItemMarkerElement(), sectionIdentifier.getValueMarkerElement(), sectionIdentifier.getDescriptionKey(), sectionIdentifier.isDeprecated());
    }

    private IssComponentAttribute(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey) {
        this(id, itemMarkerElement, valueMarkerElement, descriptionKey, false);
    }

    private IssComponentAttribute(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey, boolean deprecated) {
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
