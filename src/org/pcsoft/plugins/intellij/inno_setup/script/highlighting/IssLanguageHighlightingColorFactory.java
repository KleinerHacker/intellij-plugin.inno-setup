package org.pcsoft.plugins.intellij.inno_setup.script.highlighting;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.types.section.IssSetupPropertyClassifier;

import java.awt.*;

/**
 * Created by Christoph on 14.12.2014.
 */
public final class IssLanguageHighlightingColorFactory {

    public static final TextAttributesKey ANNOTATOR_INFO_PROPERTY_NAME_CF_COMPILER_REQUIRED = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_INFO_PROPERTY_NAME_CF_COMPILER_REQUIRED",
            new TextAttributes(IssLanguageHighlightingConstants.Colors.COMPILER, null, null, null, Font.BOLD)
    );
    public static final TextAttributesKey ANNOTATOR_INFO_PROPERTY_NAME_CF_COMPILER_STANDARD = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_INFO_PROPERTY_NAME_CF_COMPILER_STANDARD",
            new TextAttributes(IssLanguageHighlightingConstants.Colors.COMPILER, null, null, null, Font.PLAIN)
    );
    public static final TextAttributesKey ANNOTATOR_INFO_PROPERTY_NAME_CF_INSTALLER_REQUIRED = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_INFO_PROPERTY_NAME_CF_INSTALLER_REQUIRED",
            new TextAttributes(IssLanguageHighlightingConstants.Colors.SETUP, null, null, null, Font.BOLD)
    );
    public static final TextAttributesKey ANNOTATOR_INFO_PROPERTY_NAME_CF_INSTALLER_STANDARD = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_INFO_PROPERTY_NAME_CF_INSTALLER_STANDARD",
            new TextAttributes(IssLanguageHighlightingConstants.Colors.SETUP, null, null, null, Font.PLAIN)
    );
    public static final TextAttributesKey ANNOTATOR_INFO_PROPERTY_NAME_CF_COSMETIC_REQUIRED = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_INFO_PROPERTY_NAME_CF_COSMETIC_REQUIRED",
            new TextAttributes(IssLanguageHighlightingConstants.Colors.DESIGN, null, null, null, Font.BOLD)
    );
    public static final TextAttributesKey ANNOTATOR_INFO_PROPERTY_NAME_CF_COSMETIC_STANDARD = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_INFO_PROPERTY_NAME_CF_COSMETIC_STANDARD",
            new TextAttributes(IssLanguageHighlightingConstants.Colors.DESIGN, null, null, null, Font.PLAIN)
    );
    public static final TextAttributesKey ANNOTATOR_INFO_PROPERTY_NAME_CF_OBSOLETE = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_INFO_PROPERTY_NAME_CF_OBSOLETE",
            new TextAttributes(IssLanguageHighlightingConstants.Colors.IGNORABLE, null, JBColor.GRAY, EffectType.STRIKEOUT, Font.PLAIN)
    );
    public static final TextAttributesKey ANNOTATOR_INFO_PROPERTY_NAME_REQUIRED = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_INFO_PROPERTY_NAME_REQUIRED",
            new TextAttributes(IssLanguageHighlightingConstants.Colors.SETUP, null, null, null, Font.BOLD)
    );
    public static final TextAttributesKey ANNOTATOR_INFO_PROPERTY_NAME_DEPRECATED = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_INFO_PROPERTY_NAME_DEPRECATED",
            new TextAttributes(IssLanguageHighlightingConstants.Colors.IGNORABLE, null, JBColor.GRAY, EffectType.STRIKEOUT, Font.PLAIN)
    );
    public static final TextAttributesKey ANNOTATOR_INFO_PROPERTY_NAME_STANDARD = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_INFO_PROPERTY_NAME_STANDARD",
            new TextAttributes(IssLanguageHighlightingConstants.Colors.SETUP, null, null, null, Font.PLAIN)
    );

    public static LookupElementBuilder buildLookupElement(String text, TextAttributesKey textAttributesKey) {
        final TextAttributes attributes = textAttributesKey.getDefaultAttributes();

        return LookupElementBuilder.create(text)
                .withBoldness(attributes.getFontType() == Font.BOLD)
                .withStrikeoutness(attributes.getEffectType() == EffectType.STRIKEOUT)
                .withItemTextForeground(attributes.getForegroundColor() == null ? JBColor.foreground() : attributes.getForegroundColor())
                .withItemTextUnderlined(attributes.getEffectType() == EffectType.BOLD_DOTTED_LINE || attributes.getEffectType() == EffectType.BOLD_LINE_UNDERSCORE ||
                        attributes.getEffectType() == EffectType.LINE_UNDERSCORE);
    }

    //********************************************************************************************************//
    //********************************************************************************************************//
    //********************************************************************************************************//

    public static final TextAttributesKey SYNTAX_COMMENT = TextAttributesKey.createTextAttributesKey(
            "SYNTAX_COMMENT",
            new TextAttributes(IssLanguageHighlightingConstants.Colors.IGNORABLE, null, null, null, Font.ITALIC)
    );

    public static final TextAttributesKey SYNTAX_BAD_CHARACTER = TextAttributesKey.createTextAttributesKey(
            "SYNTAX_BAD_CHARACTER",
            new TextAttributes(IssLanguageHighlightingConstants.Colors.FAILURE, null, null, null, Font.BOLD)
    );

    public static final TextAttributesKey SYNTAX_NUMBER = TextAttributesKey.createTextAttributesKey(
            "SYNTAX_NUMBER",
            new TextAttributes(IssLanguageHighlightingConstants.Colors.VALUE, null, null, null, Font.PLAIN)
    );

    public static final TextAttributesKey SYNTAX_OPERATORS = TextAttributesKey.createTextAttributesKey(
            "SYNTAX_OPERATORS",
            new TextAttributes(IssLanguageHighlightingConstants.Colors.OPERATOR, null, null, null, Font.BOLD)
    );

    //********************************************************************************************************//
    //********************************************************************************************************//
    //********************************************************************************************************//

    public static final TextAttributesKey ANNOTATOR_INFO_STRING = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_INFO_STRING",
            new TextAttributes(IssLanguageHighlightingConstants.Colors.MESSAGES, null, null, null, Font.PLAIN)
    );

    public static final TextAttributesKey ANNOTATION_INFO_SECTION_TITLE = TextAttributesKey.createTextAttributesKey(
            "ANNOTATION_INFO_SECTION_TITLE",
            new TextAttributes(null, null, JBColor.BLACK, EffectType.BOLD_DOTTED_LINE, Font.BOLD)
    );

    public static final TextAttributesKey ANNOTATION_INFO_COMPILER_DIRECTIVE = TextAttributesKey.createTextAttributesKey(
            "ANNOTATION_INFO_COMPILER_DIRECTIVE",
            new TextAttributes(IssLanguageHighlightingConstants.Colors.COMPILER, null, null, null, Font.BOLD)
    );

    @NotNull
    public static TextAttributesKey getAnnotationInfoPropertyName(boolean required, boolean deprecated) {
        if (required) {
            return ANNOTATOR_INFO_PROPERTY_NAME_REQUIRED;
        } else if (deprecated) {
            return ANNOTATOR_INFO_PROPERTY_NAME_DEPRECATED;
        } else {
            return ANNOTATOR_INFO_PROPERTY_NAME_STANDARD;
        }
    }

    @NotNull
    public static TextAttributesKey getAnnotationInfoPropertyName(boolean required, @NotNull IssSetupPropertyClassifier classifier) {
        switch (classifier) {
            case Compiler:
                if (required) {
                    return ANNOTATOR_INFO_PROPERTY_NAME_CF_COMPILER_REQUIRED;
                } else {
                    return ANNOTATOR_INFO_PROPERTY_NAME_CF_COMPILER_STANDARD;
                }
            case Installer:
                if (required) {
                    return ANNOTATOR_INFO_PROPERTY_NAME_CF_INSTALLER_REQUIRED;
                } else {
                    return ANNOTATOR_INFO_PROPERTY_NAME_CF_INSTALLER_STANDARD;
                }
            case Cosmetic:
                if (required) {
                    return ANNOTATOR_INFO_PROPERTY_NAME_CF_COSMETIC_REQUIRED;
                } else {
                    return ANNOTATOR_INFO_PROPERTY_NAME_CF_COSMETIC_STANDARD;
                }
            case Obsolete:
                return ANNOTATOR_INFO_PROPERTY_NAME_CF_OBSOLETE;
            default:
                throw new RuntimeException();
        }
    }

    public static final TextAttributesKey ANNOTATOR_INFO_PROPERTY_NAME_LAN = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_INFO_PROPERTY_NAME_LAN",
            new TextAttributes(IssLanguageHighlightingConstants.Colors.MESSAGES, null, null, null, Font.PLAIN)
    );

    public static final TextAttributesKey ANNOTATOR_INFO_PROPERTY_REF_LAN = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_INFO_PROPERTY_REF_LAN",
            new TextAttributes(IssLanguageHighlightingConstants.Colors.MESSAGES, null, null, null, Font.BOLD | Font.ITALIC)
    );

    public static final TextAttributesKey ANNOTATOR_ERROR_REFERENCE = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_ERROR_REFERENCE",
            new TextAttributes(IssLanguageHighlightingConstants.Colors.FAILURE, null, null, null, Font.PLAIN)
    );

    public static final TextAttributesKey ANNOTATOR_INFO_UNUSED = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_INFO_UNUSED",
            new TextAttributes(IssLanguageHighlightingConstants.Colors.IGNORABLE, null, null, null, 0)
    );

    public static final TextAttributesKey ANNOTATOR_INFO_CONSTANT_BUILTIN_SHELL = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_INFO_CONSTANT_BUILTIN_SHELL",
            new TextAttributes(IssLanguageHighlightingConstants.Colors.BUILTIN, null, null, null, Font.ITALIC)
    );
    public static final TextAttributesKey ANNOTATOR_INFO_CONSTANT_BUILTIN_DIRECTORY = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_INFO_CONSTANT_BUILTIN_DIRECTORY",
            new TextAttributes(IssLanguageHighlightingConstants.Colors.BUILTIN, null, null, null, Font.BOLD)
    );
    public static final TextAttributesKey ANNOTATOR_INFO_CONSTANT_BUILTIN_OTHER = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_INFO_CONSTANT_BUILTIN_OTHER",
            new TextAttributes(IssLanguageHighlightingConstants.Colors.BUILTIN, null, null, null, Font.PLAIN)
    );

    public static final TextAttributesKey ANNOTATOR_INFO_CONSTANT_MESSAGE = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_INFO_CONSTANT_MESSAGE",
            new TextAttributes(IssLanguageHighlightingConstants.Colors.MESSAGES, null, null, null, Font.ITALIC)
    );

    public static final TextAttributesKey ANNOTATOR_INFO_CONSTANT_ENVIRONMENT = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_INFO_CONSTANT_ENVIRONMENT",
            new TextAttributes(IssLanguageHighlightingConstants.Colors.SYSTEM, null, null, null, Font.ITALIC)
    );

    public static final TextAttributesKey ANNOTATOR_INFO_CONSTANT_COMPILER_DIRECTIVE = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_INFO_CONSTANT_COMPILER_DIRECTIVE",
            new TextAttributes(IssLanguageHighlightingConstants.Colors.COMPILER, null, null, null, Font.ITALIC)
    );

    public static final TextAttributesKey ANNOTATION_INFO_COMPILER_DIRECTIVE_PARAMETER = TextAttributesKey.createTextAttributesKey(
            "ANNOTATION_INFO_COMPILER_DIRECTIVE_PARAMETER",
            new TextAttributes(IssLanguageHighlightingConstants.Colors.COMPILER, null, null, null, Font.ITALIC)
    );

    public static final TextAttributesKey ANNOTATOR_WARN_DEPRECATED = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_WARN_DEPRECATED",
            new TextAttributes(IssLanguageHighlightingConstants.Colors.SETUP, null, JBColor.BLACK, EffectType.STRIKEOUT, Font.BOLD)
    );

    public static final TextAttributesKey ANNOTATOR_ERROR_SECTION_NOT_ALLOWED = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_ERROR_SECTION_NOT_ALLOWED",
            new TextAttributes(null, null, IssLanguageHighlightingConstants.Colors.FAILURE, EffectType.STRIKEOUT, Font.PLAIN)
    );

    private IssLanguageHighlightingColorFactory() {
    }
}
