package org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey

object IsiSyntaxHighlighting {
    val COMMENT: TextAttributesKey         = DefaultLanguageHighlighterColors.LINE_COMMENT
    val STRING: TextAttributesKey          = DefaultLanguageHighlighterColors.STRING
    val NUMBER: TextAttributesKey          = DefaultLanguageHighlighterColors.NUMBER
    val BRACKET: TextAttributesKey         = DefaultLanguageHighlighterColors.BRACKETS
    val BRACE: TextAttributesKey           = DefaultLanguageHighlighterColors.BRACES
    val OPERATION_SIGN: TextAttributesKey  = DefaultLanguageHighlighterColors.OPERATION_SIGN
    val KEYWORD: TextAttributesKey         = DefaultLanguageHighlighterColors.KEYWORD
}
