package org.pcsoft.intellij.plugin.inno_setup.language

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.openapi.editor.colors.TextAttributesKey

object IssAnnotatorHighlighting {
    val SECTION_NAME: TextAttributesKey         = DefaultLanguageHighlighterColors.CLASS_NAME
    val PARAM_KEY: TextAttributesKey            = DefaultLanguageHighlighterColors.INSTANCE_FIELD
    val REFERENCE: TextAttributesKey            = DefaultLanguageHighlighterColors.CLASS_REFERENCE
    val FLAG: TextAttributesKey                 = DefaultLanguageHighlighterColors.STATIC_FIELD
    val PREPROCESSOR_KEYWORD: TextAttributesKey = DefaultLanguageHighlighterColors.KEYWORD
    val UNKNOWN_REFERENCE: TextAttributesKey    = CodeInsightColors.WRONG_REFERENCES_ATTRIBUTES
    val DEPRECATED: TextAttributesKey           = CodeInsightColors.DEPRECATED_ATTRIBUTES
}
