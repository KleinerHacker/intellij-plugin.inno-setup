package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssTypeAttribute implements IssDefinableSectionIdentifier {
    Name("Name", IssMarkerFactory.TypeSection.ITEM_NAME, IssMarkerFactory.TypeSection.ITEM_NAME_VALUE, "attribute.types.name"),
    Description("Description", IssMarkerFactory.TypeSection.ITEM_DESCRIPTION, IssMarkerFactory.TypeSection.ITEM_DESCRIPTION_VALUE, "attribute.types.description"),
    Flags("Flags", IssMarkerFactory.TypeSection.ITEM_FLAGS, IssMarkerFactory.TypeSection.ITEM_FLAGS_VALUE, "attribute.types.flags"),
    //Commons
    Languages(IssCommonAttribute.Languages),
    MinimalVersion(IssCommonAttribute.MinimalVersion),
    OnlyBelowVersion(IssCommonAttribute.OnlyBelowVersion);

    public static IssTypeAttribute fromId(final String id) {
        return IssSectionIdentifier.fromId(id, IssTypeAttribute.class);
    }

    public static IElementType getItemMarkerElementFromId(final String id) {
        return IssSectionIdentifier.getItemMarkerElementFromId(id, IssTypeAttribute.class);
    }

    public static IElementType getValueMarkerElementFromId(final String id) {
        return IssDefinableSectionIdentifier.getSingleValueMarkerElementFromId(id, IssTypeAttribute.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;
    private final IElementType itemMarkerElement, valueMarkerElement;

    private IssTypeAttribute(final IssDefinableSectionIdentifier sectionIdentifier) {
        this(sectionIdentifier.getId(), sectionIdentifier.getItemMarkerElement(), sectionIdentifier.getValueMarkerElement(), sectionIdentifier.getDescriptionKey(), sectionIdentifier.isDeprecated());
    }

    private IssTypeAttribute(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey) {
        this(id, itemMarkerElement, valueMarkerElement, descriptionKey, false);
    }

    private IssTypeAttribute(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey, boolean deprecated) {
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
