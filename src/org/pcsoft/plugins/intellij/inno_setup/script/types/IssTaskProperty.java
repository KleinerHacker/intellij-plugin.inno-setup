package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;

/**
 * Created by Christoph on 22.12.2014.
 */
public enum IssTaskProperty implements IssDefinableSectionIdentifier {
    Name("Name", IssMarkerFactory.TaskSection.ITEM_NAME, IssMarkerFactory.TaskSection.ITEM_NAME_VALUE, "property.tasks.name"),
    Description("Description", IssMarkerFactory.TaskSection.ITEM_DESCRIPTION, IssMarkerFactory.TaskSection.ITEM_DESCRIPTION_VALUE, "property.tasks.description"),
    DescriptionGroup("GroupDescription", IssMarkerFactory.ITEM_DEFAULT, null, "property.tasks.group_description"),
    Flags("Flags", IssMarkerFactory.TaskSection.ITEM_FLAGS, IssMarkerFactory.TaskSection.ITEM_FLAGS_VALUE, "property.tasks.flags"),
    Components("Components", IssMarkerFactory.TaskSection.ITEM_COMPONENTS, IssMarkerFactory.TaskSection.ITEM_COMPONENTS_VALUE, "property.common.components"),
    //Commons
    Languages(IssCommonProperty.Languages),
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
    private final boolean deprecated;
    private final IElementType itemMarkerElement, valueMarkerElement;

    private IssTaskProperty(final IssDefinableSectionIdentifier sectionIdentifier) {
        this(sectionIdentifier.getId(), sectionIdentifier.getItemMarkerElement(), sectionIdentifier.getValueMarkerElement(), sectionIdentifier.getDescriptionKey(), sectionIdentifier.isDeprecated());
    }

    private IssTaskProperty(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey) {
        this(id, itemMarkerElement, valueMarkerElement, descriptionKey, false);
    }

    private IssTaskProperty(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey, boolean deprecated) {
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
