package org.pcsoft.plugins.intellij.inno_setup.script.highlighting;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;

import java.awt.Color;
import java.awt.Font;

/**
 * Created by Christoph on 14.12.2014.
 */
public final class IssLanguageHighlightingColorFactory {

    public static final TextAttributesKey SYNTAX_COMMENT = TextAttributesKey.createTextAttributesKey(
            "SYNTAX_COMMENT",
            new TextAttributes(JBColor.GRAY, null, null, null, Font.ITALIC)
    );

    public static final TextAttributesKey SYNTAX_BAD_CHARACTER = TextAttributesKey.createTextAttributesKey(
            "SYNTAX_BAD_CHARACTER",
            new TextAttributes(JBColor.RED, null, null, null, Font.BOLD)
    );

    public static final TextAttributesKey SYNTAX_STRING = TextAttributesKey.createTextAttributesKey(
            "SYNTAX_STRING",
            new TextAttributes(JBColor.GREEN, null, null, null, Font.PLAIN)
    );

    public static final TextAttributesKey SYNTAX_SECTION_TITLE = TextAttributesKey.createTextAttributesKey(
            "SYNTAX_SECTION_TITLE",
            new TextAttributes(null, null, JBColor.BLACK, EffectType.BOLD_DOTTED_LINE, Font.BOLD)
    );

    public static final TextAttributesKey SYNTAX_CD = TextAttributesKey.createTextAttributesKey(
            "SYNTAX_CD",
            new TextAttributes(new JBColor(new Color(0x00A0C0), new Color(0x00A0C0)), null, null, null, Font.PLAIN)
    );

    public static final TextAttributesKey SYNTAX_OPERATORS = TextAttributesKey.createTextAttributesKey(
            "SYNTAX_OPERATORS",
            new TextAttributes(null, null, null, null, Font.BOLD)
    );

    //********************************************************************************************************//
    //********************************************************************************************************//
    //********************************************************************************************************//

    public static final TextAttributesKey ANNOTATOR_INFO_PROPERTY_NAME = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_INFO_PROPERTY_NAME",
            new TextAttributes(JBColor.BLUE, null, null, null, Font.BOLD)
    );

    public static final TextAttributesKey ANNOTATOR_INFO_PROPERTY_LAN = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_INFO_PROPERTY_LAN",
            new TextAttributes(JBColor.BLUE.brighter(), null, null, null, Font.BOLD | Font.ITALIC)
    );

    public static final TextAttributesKey ANNOTATOR_ERROR_REFERENCE = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_ERROR_REFERENCE",
            new TextAttributes(JBColor.RED, null, null, null, Font.BOLD)
    );

    public static final TextAttributesKey ANNOTATOR_INFO_CONSTANT = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_INFO_CONSTANT",
            new TextAttributes(JBColor.PINK, null, null, null, Font.BOLD)
    );

    public static final TextAttributesKey ANNOTATOR_WARN_DEPRECATED = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_WARN_DEPRECATED",
            new TextAttributes(JBColor.BLUE, null, JBColor.BLACK, EffectType.STRIKEOUT, Font.BOLD)
    );

    public static final TextAttributesKey ANNOTATOR_ERROR_SECTION_NOT_ALLOWED = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_ERROR_SECTION_NOT_ALLOWED",
            new TextAttributes(null, null, JBColor.RED, EffectType.STRIKEOUT, Font.PLAIN)
    );

    private IssLanguageHighlightingColorFactory() {
    }
}
