/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor

import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.openapi.editor.colors.TextAttributesKey

/**
 * Editor highlighting attribute keys used by the ISPP annotator. These intentionally reuse the **same external
 * string IDs** as the script module's `IsSectionAnnotatorHighlighting` (e.g. `ISS_PREPROCESSOR_DIRECTIVE`):
 * `TextAttributesKey.createTextAttributesKey(id)` is keyed solely by that string, so both modules resolve to
 * the identical attributes (defined by the bundled colour schemes in the `:plugin` module) without either
 * module depending on the other.
 */
object IsPreprocessorAnnotatorHighlighting {

    /** `#define` / `#include` directive marker (`#` + keyword), and the `#` of a `{#Name}` reference. */
    val PREPROCESSOR_DIRECTIVE: TextAttributesKey =
        TextAttributesKey.createTextAttributesKey("ISS_PREPROCESSOR_DIRECTIVE")

    /** The defined name in a `#define` declaration — always italic. */
    val DEFINE_NAME: TextAttributesKey =
        TextAttributesKey.createTextAttributesKey("ISS_DEFINE_NAME")

    /** A literal boolean word (`true`/`false`/`yes`/`no`) used directly in a `#if`/`#elif` condition. */
    val BOOLEAN_LITERAL: TextAttributesKey =
        TextAttributesKey.createTextAttributesKey("ISS_BOOLEAN_LITERAL")

    /** Unresolved/unknown reference — red squiggle (platform attributes). */
    val UNKNOWN_REFERENCE: TextAttributesKey = CodeInsightColors.WRONG_REFERENCES_ATTRIBUTES

    /** Unused elements (an unused `#define`) — gray, like Java's unused. */
    val UNUSED: TextAttributesKey =
        TextAttributesKey.createTextAttributesKey("ISS_UNUSED", CodeInsightColors.NOT_USED_ELEMENT_ATTRIBUTES)
}
