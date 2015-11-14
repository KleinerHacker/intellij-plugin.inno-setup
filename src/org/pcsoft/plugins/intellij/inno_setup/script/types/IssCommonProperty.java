package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;

/**
 * Created by Christoph on 27.12.2014.
 */
enum IssCommonProperty implements IssDefinableSectionIdentifier {
    Languages("Languages", IssMarkerFactory.PROPERTY_UNKNOWN, null,
            "common.property.languages", IssValueType.DirectMultiple),
    MinimalVersion("MinVersion", IssMarkerFactory.PROPERTY_UNKNOWN, null,
            "common.property.min_version", IssValueType.DirectSingle),
    OnlyBelowVersion("OnlyBelowVersion", IssMarkerFactory.PROPERTY_UNKNOWN, null,
            "common.property.only_below_version", IssValueType.DirectSingle);

    private final String id, descriptionKey;
    private final boolean deprecated, required;
    private final IElementType itemMarkerElement, valueMarkerElement;
    private final IssValueType valueType;

    private IssCommonProperty(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey,
                              IssValueType valueType) {
        this(id, itemMarkerElement, valueMarkerElement, descriptionKey, valueType, false);
    }

    private IssCommonProperty(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey,
                              IssValueType valueType, boolean required) {
        this(id, itemMarkerElement, valueMarkerElement, descriptionKey, valueType, required, false);
    }

    private IssCommonProperty(String id, IElementType itemMarkerElement, IElementType valueMarkerElement, String descriptionKey,
                              IssValueType valueType, boolean required, boolean deprecated) {
        this.id = id;
        this.descriptionKey = descriptionKey;
        this.deprecated = deprecated;
        this.required = required;
        this.itemMarkerElement = itemMarkerElement;
        this.valueMarkerElement = valueMarkerElement;
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

    @Nullable
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
