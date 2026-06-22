/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.intellij.plugin.inno_setup.language.parser.section

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.openapi.editor.colors.TextAttributesKey

/**
 * Holds editor highlighting attributes for Inno Setup inspections.
 */
object IsSectionAnnotatorHighlighting {
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val SECTION_NAME: TextAttributesKey = DefaultLanguageHighlighterColors.CLASS_NAME
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val PARAM_KEY: TextAttributesKey = DefaultLanguageHighlighterColors.INSTANCE_FIELD
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val REFERENCE: TextAttributesKey = DefaultLanguageHighlighterColors.CLASS_REFERENCE
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val FLAG: TextAttributesKey = DefaultLanguageHighlighterColors.STATIC_FIELD

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

    /**
     * The `cm` keyword of a `{cm:…}` constant — italic (default attributes provided by the bundled
     * color schemes, layered over the constant's reference colour).
     */
    val CUSTOM_MESSAGE_PREFIX: TextAttributesKey =
        TextAttributesKey.createTextAttributesKey("ISS_CM_PREFIX")

    /**
     * Returns or performs the public behavior represented by this member.
     */
    val UNKNOWN_REFERENCE: TextAttributesKey = CodeInsightColors.WRONG_REFERENCES_ATTRIBUTES
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val DEPRECATED: TextAttributesKey = CodeInsightColors.DEPRECATED_ATTRIBUTES

    /** Unused elements (trailing semicolons, unused #define, empty sections) — gray, like Java's unused. */
    val UNUSED: TextAttributesKey =
        TextAttributesKey.createTextAttributesKey("ISS_UNUSED", CodeInsightColors.NOT_USED_ELEMENT_ATTRIBUTES)

    /**
     * Background tint for text pulled in via `#include` in the read-only *effective script* view — a light,
     * translucent blue (same hue as the directive colour). Default attributes are provided by the bundled
     * color schemes (colorSchemes/IssDefault.xml, colorSchemes/IssDarcula.xml).
     */
    val INCLUDE_SEGMENT_BACKGROUND: TextAttributesKey =
        TextAttributesKey.createTextAttributesKey("ISS_INCLUDE_SEGMENT_BACKGROUND")
}
