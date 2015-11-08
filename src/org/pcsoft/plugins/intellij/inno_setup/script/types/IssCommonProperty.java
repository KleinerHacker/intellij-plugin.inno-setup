package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;

/**
 * Created by Christoph on 27.12.2014.
 */
enum IssCommonProperty implements IssDefinableSectionIdentifier {
    Languages("Languages", IssMarkerFactory.ITEM_DEFAULT, null, "common.property.languages"),
    MinimalVersion("MinVersion", IssMarkerFactory.ITEM_DEFAULT, null, "common.property.min_version"),
    OnlyBelowVersion("OnlyBelowVersion", IssMarkerFactory.ITEM_DEFAULT, null, "common.property.only_below_version");

    private final String id, descriptonKey;
    private final boolean deprecated;
    private final IElementType itemMarkerElement, valueMarkerElement;

    private IssCommonProperty(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptonKey) {
        this(id, itemMarkerElement, valueMarkerElement, descriptonKey, false);
    }

    private IssCommonProperty(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptonKey, boolean deprecated) {
        this.id = id;
        this.descriptonKey = descriptonKey;
        this.deprecated = deprecated;
        this.itemMarkerElement = itemMarkerElement;
        this.valueMarkerElement = valueMarkerElement;
    }

    @NotNull
    @Override
    public String getId() {
        return id;
    }

    @NotNull
    @Override
    public String getDescriptionKey() {
        return descriptonKey;
    }


    @NotNull
    @Override
    public IElementType getItemMarkerElement() {
        return itemMarkerElement;
    }

    @Nullable
    @Override
    public IElementType getValueMarkerElement() {
        return valueMarkerElement;
    }

    @Override
    public boolean isDeprecated() {
        return deprecated;
    }
}
