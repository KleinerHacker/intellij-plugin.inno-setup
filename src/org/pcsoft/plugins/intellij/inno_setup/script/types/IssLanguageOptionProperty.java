package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;

/**
 * Created by Christoph on 22.12.2014.
 */
public enum IssLanguageOptionProperty implements IssPropertyIdentifier {
    LanguageName("LanguageName", IssMarkerFactory.LanguageOptionSection.PROPERTY_LANGUAGENAME, IssMarkerFactory.LanguageOptionSection.PROPERTY_LANGUAGENAME_VALUE,
            "languageoption.property.languagename", IssValueType.DirectMultiple, true),
    LanguageID("LanguageID", IssMarkerFactory.LanguageOptionSection.PROPERTY_LANGUAGEID, IssMarkerFactory.LanguageOptionSection.PROPERTY_LANGUAGEID_VALUE,
            "languageoption.property.languageid", IssValueType.HexBinary, true),
    LanguageCodePage("LanguageCodePage", IssMarkerFactory.LanguageOptionSection.PROPERTY_LANGUAGECODEPAGE, IssMarkerFactory.LanguageOptionSection.PROPERTY_LANGUAGECODEPAGE_VALUE,
            "languageoption.property.languagecodepage", IssValueType.Integer, true),
    DialogFontName("DialogFontName", IssMarkerFactory.LanguageOptionSection.PROPERTY_DIALOGFONTNAME, IssMarkerFactory.LanguageOptionSection.PROPERTY_DIALOGFONTNAME_VALUE,
            "languageoption.property.dialogfontname", IssValueType.DirectMultiple),
    DialogFontSize("DialogFontSize", IssMarkerFactory.LanguageOptionSection.PROPERTY_DIALOGFONTSIZE, IssMarkerFactory.LanguageOptionSection.PROPERTY_DIALOGFONTSIZE_VALUE,
            "languageoption.property.dialogfontsize", IssValueType.Integer),
    WelcomeFontName("WelcomeFontName", IssMarkerFactory.LanguageOptionSection.PROPERTY_WELCOMEFONTNAME, IssMarkerFactory.LanguageOptionSection.PROPERTY_WELCOMEFONTNAME_VALUE,
            "languageoption.property.welcomefontname", IssValueType.DirectMultiple),
    WelcomeFontSize("WelcomeFontSize", IssMarkerFactory.LanguageOptionSection.PROPERTY_WELCOMEFONTSIZE, IssMarkerFactory.LanguageOptionSection.PROPERTY_WELCOMEFONTSIZE_VALUE,
            "languageoption.property.welcomefontsize", IssValueType.Integer),
    TitleFontName("TitleFontName", IssMarkerFactory.LanguageOptionSection.PROPERTY_TITLEFONTNAME, IssMarkerFactory.LanguageOptionSection.PROPERTY_TITLEFONTNAME_VALUE,
            "languageoption.property.titlefontname", IssValueType.DirectMultiple),
    TitleFontSize("TitleFontSize", IssMarkerFactory.LanguageOptionSection.PROPERTY_TITLEFONTSIZE, IssMarkerFactory.LanguageOptionSection.PROPERTY_TITLEFONTSIZE_VALUE,
            "languageoption.property.titlefontsize", IssValueType.Integer),
    CopyrightFontName("CopyrightFontName", IssMarkerFactory.LanguageOptionSection.PROPERTY_COPYRIGHTFONTNAME, IssMarkerFactory.LanguageOptionSection.PROPERTY_COPYRIGHTFONTNAME_VALUE,
            "languageoption.property.copyrightfontname", IssValueType.DirectMultiple),
    CopyrightFontSize("CopyrightFontSize", IssMarkerFactory.LanguageOptionSection.PROPERTY_COPYRIGHTFONTSIZE, IssMarkerFactory.LanguageOptionSection.PROPERTY_COPYRIGHTFONTSIZE_VALUE,
            "languageoption.property.copyrightfontsize", IssValueType.Integer),
    RightToLeft("RightToLeft", IssMarkerFactory.LanguageOptionSection.PROPERTY_RIGHTTOLEFT, IssMarkerFactory.LanguageOptionSection.PROPERTY_RIGHTTOLEFT_VALUE,
            "languageoption.property.righttoleft", IssValueType.Boolean),
    ;

    public static IssLanguageOptionProperty fromId(final String id) {
        return IssPropertyIdentifier.fromId(id, IssLanguageOptionProperty.class);
    }

    public static IElementType getPropertyMarkerElementFromId(final String id) {
        return IssPropertyIdentifier.getItemMarkerElementFromId(id, IssLanguageOptionProperty.class);
    }

    public static IElementType getPropertyValueMarkerElementFromId(final String id) {
        return IssPropertyIdentifier.getSingleValueMarkerElementFromId(id, IssLanguageOptionProperty.class);
    }

    private final String id, descriptionKey;
    private final IssValueType valueType;

    private final IElementType propertyMarkerElement, propertyValueMarkerElement;
    private final boolean required, deprecated;
    private IssLanguageOptionProperty(String id, IElementType propertyMarkerElement, IElementType propertyValueMarkerElement,
                                      String descriptionKey, IssValueType valueType) {
        this(id, propertyMarkerElement, propertyValueMarkerElement, descriptionKey, valueType, false);
    }

    private IssLanguageOptionProperty(String id, IElementType propertyMarkerElement, IElementType propertyValueMarkerElement,
                                      String descriptionKey, IssValueType valueType, boolean required) {
        this(id, propertyMarkerElement, propertyValueMarkerElement, descriptionKey, valueType, required, false);
    }

    private IssLanguageOptionProperty(String id, IElementType propertyMarkerElement, IElementType propertyValueMarkerElement,
                                      String descriptionKey, IssValueType valueType, boolean required, boolean deprecated) {
        this.id = id;
        this.descriptionKey = descriptionKey;
        this.propertyMarkerElement = propertyMarkerElement;
        this.propertyValueMarkerElement = propertyValueMarkerElement;
        this.valueType = valueType;
        this.required = required;
        this.deprecated = deprecated;
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
        return propertyMarkerElement;
    }


    @Override
    public IElementType getPropertyValueMarkerElement() {
        return propertyValueMarkerElement;
    }

    @NotNull
    @Override
    public IssValueType getValueType() {
        return valueType;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public boolean isDeprecated() {
        return deprecated;
    }

    @Override
    public String toString() {
        return id;
    }
}
