package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssLanguageProperty implements IssPropertyIdentifier {
    Name("Name", IssMarkerFactory.LanguageSection.PROPERTY_NAME, IssMarkerFactory.LanguageSection.PROPERTY_NAME_VALUE,
            "language.property.name", IssValueType.DirectSingle, true),
    MessageFile("MessageFile", IssMarkerFactory.LanguageSection.PROPERTY_MEESAGEFILE, IssMarkerFactory.LanguageSection.PROPERTY_MEESAGEFILE_VALUE,
            "language.property.messagefile", IssValueType.String, true),
    LicenceFile("LicenceFile", IssMarkerFactory.LanguageSection.PROPERTY_LICENCEFILE, IssMarkerFactory.LanguageSection.PROPERTY_LICENCEFILE_VALUE,
            "language.property.licencefile", IssValueType.String),
    InfoBeforeFile("InfoBeforeFile", IssMarkerFactory.LanguageSection.PROPERTY_INFOBEFOREFILE, IssMarkerFactory.LanguageSection.PROPERTY_INFOBEFOREFILE_VALUE,
            "language.property.infobeforefile", IssValueType.String),
    InfoAfterFile("InfoAfterFile", IssMarkerFactory.LanguageSection.PROPERTY_INFOAFTERFILE, IssMarkerFactory.LanguageSection.PROPERTY_INFOAFTERFILE_VALUE,
            "language.property.infoafterfile", IssValueType.String),
    ;

    public static IssLanguageProperty fromId(final String id) {
        return IssPropertyIdentifier.fromId(id, IssLanguageProperty.class);
    }

    public static IElementType getPropertyMarkerElementFromId(final String id) {
        return IssPropertyIdentifier.getItemMarkerElementFromId(id, IssLanguageProperty.class);
    }

    public static IElementType getPropertyValueMarkerElementFromId(final String id) {
        return IssPropertyIdentifier.getSingleValueMarkerElementFromId(id, IssLanguageProperty.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated, required;
    private final IElementType itemMarkerElement, propertyValueMarkerElement;
    private final IssValueType valueType;

    private IssLanguageProperty(final IssPropertyIdentifier sectionIdentifier) {
        this(sectionIdentifier.getId(), sectionIdentifier.getPropertyMarkerElement(), sectionIdentifier.getPropertyValueMarkerElement(),
                sectionIdentifier.getDescriptionKey(), sectionIdentifier.getValueType(), sectionIdentifier.isRequired(),
                sectionIdentifier.isDeprecated());
    }

    private IssLanguageProperty(String id, IElementType itemMarkerElement, IElementType propertyValueMarkerElement, String descriptionKey,
                                IssValueType valueType) {
        this(id, itemMarkerElement, propertyValueMarkerElement, descriptionKey, valueType, false);
    }

    private IssLanguageProperty(String id, IElementType itemMarkerElement, IElementType propertyValueMarkerElement, String descriptionKey,
                                IssValueType valueType, boolean required) {
        this(id, itemMarkerElement, propertyValueMarkerElement, descriptionKey, valueType, required, false);
    }

    private IssLanguageProperty(String id, IElementType itemMarkerElement, IElementType propertyValueMarkerElement, String descriptionKey,
                                IssValueType valueType, boolean required, boolean deprecated) {
        this.id = id;
        this.deprecated = deprecated;
        this.required = required;
        this.itemMarkerElement = itemMarkerElement;
        this.propertyValueMarkerElement = propertyValueMarkerElement;
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
    public IElementType getPropertyMarkerElement() {
        return itemMarkerElement;
    }

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
