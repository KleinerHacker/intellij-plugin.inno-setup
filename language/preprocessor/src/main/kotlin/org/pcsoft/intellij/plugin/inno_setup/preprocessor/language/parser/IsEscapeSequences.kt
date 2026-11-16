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

package org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser

/**
 * Patterns of the escape sequences of Inno Setup, shared by the script and the preprocessor language.
 *
 * - `{{` stands for a literal `{`; a `}` is never escaped
 * - `""` inside a double-quoted value (and `''` inside a single-quoted ISPP string) stands for a literal quote
 * - `%7d`, `%2c`, `%7c` stand for a literal `}`, `,` and `|` inside the arguments of a constant
 * - `%n`, `%%`, `%1`–`%9` are the escapes of message texts
 */
object IsEscapeSequences {

    /**
     * A `{{…}}` with two closing braces: `{{` is an escaped `{`, but a closing `}` is never escaped, so
     * Inno Setup produces `{…}}` — the second `}` stays in the text. The text between the braces holds no brace.
     */
    val DOUBLE_CLOSED_BRACE: Regex = Regex("""\{\{[^{}]*}}""")

    /** The percent escapes: `%n`, `%%`, `%1`–`%9` and `%7d`, `%7c`, `%2c`. */
    val PERCENT_ESCAPE: Regex = Regex("%(?:[n%1-9]|7[dDcC]|2[cC])")

    /** Warning text for a [DOUBLE_CLOSED_BRACE]. */
    const val DOUBLE_CLOSED_BRACE_MESSAGE: String =
        "'{{…}}' is not an escaped '{…}': '{{' becomes '{', but a closing '}' is never escaped, " +
                "so the result is '{…}}' with a stray '}'. Use a single '}'"
}
