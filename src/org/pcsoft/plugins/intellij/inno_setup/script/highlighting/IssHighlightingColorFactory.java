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
public final class IssHighlightingColorFactory {

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
            new TextAttributes(new JBColor(new Color(0x008000), new Color(0x008000)), null, null, null, Font.PLAIN)
    );

    public static final TextAttributesKey SYNTAX_SECTION_TITLE = TextAttributesKey.createTextAttributesKey(
            "SYNTAX_SECTION_TITLE",
            new TextAttributes(null, null, JBColor.BLACK, EffectType.BOLD_DOTTED_LINE, Font.BOLD)
    );

    public static final TextAttributesKey SYNTAX_CP = TextAttributesKey.createTextAttributesKey(
            "SYNTAX_CP",
            new TextAttributes(new JBColor(new Color(0x00A0C0), new Color(0x00A0C0)), null, null, null, Font.PLAIN)
    );

    public static final TextAttributesKey SYNTAX_OPERATORS = TextAttributesKey.createTextAttributesKey(
            "SYNTAX_OPERATORS",
            new TextAttributes(null, null, null, null, Font.BOLD)
    );

    public static final TextAttributesKey ANNOTATOR_INFO_SECTION_ITEM_NAME = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_INFO_SECTION_ITEM_NAME",
            new TextAttributes(JBColor.BLUE, null, null, null, Font.BOLD)
    );

    public static final TextAttributesKey ANNOTATOR_ERROR_REFERENCE = TextAttributesKey.createTextAttributesKey(
            "ANNOTATOR_ERROR_REFERENCE",
            new TextAttributes(JBColor.RED, null, null, null, Font.BOLD)
    );

    private IssHighlightingColorFactory() {
    }
}
