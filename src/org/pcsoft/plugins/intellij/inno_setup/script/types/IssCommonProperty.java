package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;

/**
 * Created by Christoph on 27.12.2014.
 */
public enum IssCommonProperty implements IssPropertyIdentifier {
    Languages("Languages", IssMarkerFactory.CommonSection.PROPERTY_LANGUAGES, IssMarkerFactory.CommonSection.PROPERTY_LANGUAGES_VALUE,
            "common.property.languages", IssValueType.DirectMultiple),
    MinimalVersion("MinVersion", IssMarkerFactory.CommonSection.PROPERTY_MINVERSION, IssMarkerFactory.CommonSection.PROPERTY_MINVERSION_VALUE,
            "common.property.min_version", IssValueType.DirectSingleWithNumber),
    OnlyBelowVersion("OnlyBelowVersion", IssMarkerFactory.CommonSection.PROPERTY_ONLYBELOWVERSION, IssMarkerFactory.CommonSection.PROPERTY_ONLYBELOWVERSION_VALUE,
            "common.property.only_below_version", IssValueType.DirectSingleWithNumber),
    ComponentReference("Components", IssMarkerFactory.CommonSection.PROPERTY_COMPONENTS, IssMarkerFactory.CommonSection.PROPERTY_COMPONENTS_VALUE,
            "common.property.components", IssValueType.DirectMultiple),
    TaskReference("Tasks", IssMarkerFactory.CommonSection.PROPERTY_TASKS, IssMarkerFactory.CommonSection.PROPERTY_TASKS_VALUE,
            "common.property.tasks", IssValueType.DirectMultiple),;

    private final String id, descriptionKey;
    private final boolean deprecated, required;
    private final IElementType itemMarkerElement, propertyValueMarkerElement;
    private final IssValueType valueType;

    private IssCommonProperty(String id, IElementType itemMarkerElement, IElementType propertyValueMarkerElement, String descriptionKey,
                              IssValueType valueType) {
        this(id, itemMarkerElement, propertyValueMarkerElement, descriptionKey, valueType, false);
    }

    private IssCommonProperty(String id, IElementType itemMarkerElement, IElementType propertyValueMarkerElement, String descriptionKey,
                              IssValueType valueType, boolean required) {
        this(id, itemMarkerElement, propertyValueMarkerElement, descriptionKey, valueType, required, false);
    }

    private IssCommonProperty(String id, IElementType itemMarkerElement, IElementType propertyValueMarkerElement, String descriptionKey,
                              IssValueType valueType, boolean required, boolean deprecated) {
        this.id = id;
        this.descriptionKey = descriptionKey;
        this.deprecated = deprecated;
        this.required = required;
        this.itemMarkerElement = itemMarkerElement;
        this.propertyValueMarkerElement = propertyValueMarkerElement;
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

    @Nullable
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
