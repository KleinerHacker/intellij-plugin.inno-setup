package org.pcsoft.plugins.intellij.iss.core.language.highlighting;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;

public class IssHighlighting {
    public static final TextAttributesKey OPERATOR = TextAttributesKey.createTextAttributesKey("ISS_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey SPLITTER = TextAttributesKey.createTextAttributesKey("ISS_SPLITTER", DefaultLanguageHighlighterColors.SEMICOLON);
    public static final TextAttributesKey BRACES = TextAttributesKey.createTextAttributesKey("ISS_BRACES", DefaultLanguageHighlighterColors.BRACES);
    public static final TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey("ISS_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey NUMBER = TextAttributesKey.createTextAttributesKey("ISS_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey("ISS_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey CONST = TextAttributesKey.createTextAttributesKey("ISS_CONST", DefaultLanguageHighlighterColors.CONSTANT);
    public static final TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey("ISS_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey LABEL = TextAttributesKey.createTextAttributesKey("ISS_LABEL", DefaultLanguageHighlighterColors.LABEL);
    public static final TextAttributesKey PREPROCESSOR = TextAttributesKey.createTextAttributesKey("ISS_PREPROCESSOR",
            new TextAttributes(JBColor.BLUE, null, null, null, 0));
}
