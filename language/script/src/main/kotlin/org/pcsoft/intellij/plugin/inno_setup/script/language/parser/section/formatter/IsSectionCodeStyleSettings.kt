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

package org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.formatter

import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CustomCodeStyleSettings

/**
 * Custom Inno Setup code-style options, shown under *Settings ▸ Editor ▸ Code Style ▸ Inno Setup* and read by
 * [IsSectionFormattingModelBuilder] (inline spacing) and [IsFormatterPostFormatProcessor] (line structure and
 * preprocessor operator spacing).
 *
 * Every option defaults to `true` — the standard configuration requested for the plugin:
 * exactly one blank line between sections, brackets tight around the section name, one space around `=`,
 * `key: value` / `; ` spacing in parameter lines, no leading indentation before a key, and one space around
 * the arithmetic operators of preprocessor expressions.
 */
class IsSectionCodeStyleSettings(container: CodeStyleSettings) :
    CustomCodeStyleSettings(TAG, container) {

    /** Rule 1 — collapse the gap between two section blocks to exactly one blank line. */
    @JvmField
    var BLANK_LINE_BETWEEN_SECTIONS: Boolean = true

    /** Rule 2 — no spaces inside the section header brackets (`[Setup]`, not `[ Setup ]`). */
    @JvmField
    var NO_SPACE_INSIDE_BRACKETS: Boolean = true

    /** Rule 3.1 — exactly one space on each side of `=` in a directive entry (`Key = Value`). */
    @JvmField
    var SPACE_AROUND_ASSIGN: Boolean = true

    /** Rule 4.3 — no space before, exactly one space after `:` in a parameter pair (`Key: Value`). */
    @JvmField
    var SPACE_AFTER_COLON: Boolean = true

    /** Rule 4.1 — no space before, exactly one space after `;` between parameter pairs (`…; Key: …`). */
    @JvmField
    var SPACE_AFTER_SEMICOLON: Boolean = true

    /** Rules 3.2 / 4.2 — a key/parameter line starts at column 0 (no leading whitespace before the first key). */
    @JvmField
    var TRIM_LEADING_KEY_SPACE: Boolean = true

    /** Rule 5 — exactly one space around the arithmetic operators (`+ - * / %`) of preprocessor expressions. */
    @JvmField
    var SPACE_AROUND_PP_OPERATORS: Boolean = true

    /** Rule 6 — no spaces inside preprocessor `()` / `[]` and the `#for {…}` header braces (mirrors Rule 2). */
    @JvmField
    var PP_NO_SPACE_INSIDE_BRACKETS: Boolean = true

    /** Rule 7 — one space around the assignment `=` in a `#for` header (mirrors Rule 3.1). */
    @JvmField
    var PP_SPACE_AROUND_ASSIGN: Boolean = true

    /** Rule 8 — no space before, one after the `;` separators of a `#for` header (mirrors Rule 4.1). */
    @JvmField
    var PP_SPACE_AFTER_SEMICOLON: Boolean = true

    companion object {
        /** Serialization tag under which these options are stored inside the shared code-style settings. */
        const val TAG = "IsSectionCodeStyleSettings"
    }
}
