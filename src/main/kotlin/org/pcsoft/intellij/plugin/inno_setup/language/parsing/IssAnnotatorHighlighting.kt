package org.pcsoft.intellij.plugin.inno_setup.language.parsing

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.openapi.editor.colors.TextAttributesKey

object IssAnnotatorHighlighting {
    val SECTION_NAME: TextAttributesKey         = DefaultLanguageHighlighterColors.CLASS_NAME
    val PARAM_KEY: TextAttributesKey            = DefaultLanguageHighlighterColors.INSTANCE_FIELD
    val REFERENCE: TextAttributesKey            = DefaultLanguageHighlighterColors.CLASS_REFERENCE
    val FLAG: TextAttributesKey                 = DefaultLanguageHighlighterColors.STATIC_FIELD

    // ISPP preprocessor — own keys (Kotlin "extension function" blue, italic name).
    // Default attributes are provided by the bundled color schemes
    // (colorSchemes/IssDefault.xml, colorSchemes/IssDarcula.xml) so they adapt to dark/light.
    /** `#define` / `#include` directive marker (`#` + keyword), and the `#` of a `{#Name}` reference. */
    val PREPROCESSOR_DIRECTIVE: TextAttributesKey =
        TextAttributesKey.createTextAttributesKey("ISS_PREPROCESSOR_DIRECTIVE")
    /** The name part of a `{#Name}` reference — blue + italic. */
    val ISPP_REFERENCE_NAME: TextAttributesKey =
        TextAttributesKey.createTextAttributesKey("ISS_ISPP_REFERENCE_NAME")
    /** The defined name in a `#define` declaration — always italic. */
    val DEFINE_NAME: TextAttributesKey =
        TextAttributesKey.createTextAttributesKey("ISS_DEFINE_NAME")

    val UNKNOWN_REFERENCE: TextAttributesKey    = CodeInsightColors.WRONG_REFERENCES_ATTRIBUTES
    val DEPRECATED: TextAttributesKey           = CodeInsightColors.DEPRECATED_ATTRIBUTES
}
