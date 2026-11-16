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

/**
 * Escape sequences of Inno Setup script values.
 *
 * - `{{` stands for a literal `{`; a `}` is never escaped
 * - `""` inside a quoted value stands for a literal double quote
 * - `%7d`, `%2c`, `%7c` inside the arguments of a constant stand for a literal `}`, `,` and `|`
 * - `%n`, `%%`, `%1`–`%9` are message escapes of the `[Messages]` and `[CustomMessages]` sections
 */
object IsSectionEscapes {

    /** Escapes of a constant argument: `%7d` (`}`), `%2c` (`,`) and `%7c` (`|`), case-insensitive. */
    val CONSTANT_ARGUMENT_ESCAPE: Regex = Regex("%(?:7[dDcC]|2[cC])")

    /** Escapes of a message text: `%n` (line break), `%%` (literal `%`) and the arguments `%1`–`%9`. */
    val MESSAGE_ESCAPE: Regex = Regex("%(?:[nN%]|[1-9])")

    /**
     * A `{{…}}` with two closing braces: `{{` is an escaped `{`, but a closing `}` is never escaped, so
     * Inno Setup produces `{…}}` — the second `}` stays in the text. The text between the braces holds no brace.
     */
    val DOUBLE_CLOSED_BRACE: Regex = Regex("""\{\{[^{}]*}}""")

    /** Lower-case names of the sections whose values are message texts (the `%` escapes apply there only). */
    val MESSAGE_SECTIONS: Set<String> = setOf("messages", "custommessages")
}

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
