package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;

/**
 * Created by Christoph on 22.12.2014.
 */
public enum IssTaskAttribute implements IssDefinableSectionIdentifier {
    Name("Name", IssMarkerFactory.TaskSection.ITEM_NAME, IssMarkerFactory.TaskSection.ITEM_NAME_VALUE, "attribute.tasks.name"),
    Description("Description", IssMarkerFactory.TaskSection.ITEM_DESCRIPTION, IssMarkerFactory.TaskSection.ITEM_DESCRIPTION_VALUE, "attribute.tasks.description"),
    DescriptionGroup("GroupDescription", IssMarkerFactory.ITEM_DEFAULT, null, "attribute.tasks.group_description"),
    Flags("Flags", IssMarkerFactory.TaskSection.ITEM_FLAGS, IssMarkerFactory.TaskSection.ITEM_FLAGS_VALUE, "attribute.tasks.flags"),
    Components("Components", IssMarkerFactory.TaskSection.ITEM_COMPONENTS, IssMarkerFactory.TaskSection.ITEM_COMPONENTS_VALUE, "attribute.common.components"),
    //Commons
    Languages(IssCommonAttribute.Languages),
    MinimalVersion(IssCommonAttribute.MinimalVersion),
    OnlyBelowVersion(IssCommonAttribute.OnlyBelowVersion);

    public static IssTaskAttribute fromId(final String id) {
        return IssSectionIdentifier.fromId(id, IssTaskAttribute.class);
    }

    public static IElementType getItemMarkerElementFromId(final String id) {
        return IssSectionIdentifier.getItemMarkerElementFromId(id, IssTaskAttribute.class);
    }

    public static IElementType getValueMarkerElementFromId(final String id) {
        return IssDefinableSectionIdentifier.getSingleValueMarkerElementFromId(id, IssTaskAttribute.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;
    private final IElementType itemMarkerElement, valueMarkerElement;

    private IssTaskAttribute(final IssDefinableSectionIdentifier sectionIdentifier) {
        this(sectionIdentifier.getId(), sectionIdentifier.getItemMarkerElement(), sectionIdentifier.getValueMarkerElement(), sectionIdentifier.getDescriptionKey(), sectionIdentifier.isDeprecated());
    }

    private IssTaskAttribute(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey) {
        this(id, itemMarkerElement, valueMarkerElement, descriptionKey, false);
    }

    private IssTaskAttribute(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey, boolean deprecated) {
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
