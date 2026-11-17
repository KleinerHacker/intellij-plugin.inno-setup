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

package org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section

import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionParamValue

// The escape sequences of Inno Setup values — `{{` (literal `{`, a closing `}` is never escaped), `""` inside a
// quoted value (literal quote) and the percent escapes — are tokens of the section lexer. Their patterns shared
// with the preprocessor language live in `IsEscapeSequences`.

/**
 * The text of this value as the compiler sees it: surrounding quotes are removed, `""` inside a quoted
 * value becomes `"` and `{{` becomes `{`.
 */
val IsSectionParamValue.unescapedText: String
    get() = text.unescapeIssValue()

/**
 * Resolves the escapes of a script value: surrounding double quotes are removed, `""` inside a quoted value
 * becomes `"` and `{{` becomes `{`. The text is trimmed first.
 */
fun String.unescapeIssValue(): String {
    val trimmed = trim()
    val quoted = trimmed.length >= 2 && trimmed.startsWith('"') && trimmed.endsWith('"')
    val body = if (quoted) trimmed.substring(1, trimmed.length - 1).replace("\"\"", "\"") else trimmed
    return body.replace("{{", "{")
}
