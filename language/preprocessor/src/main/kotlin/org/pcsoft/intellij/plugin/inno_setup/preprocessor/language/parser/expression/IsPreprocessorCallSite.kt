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

package org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression

/**
 * A function call surrounding a caret position inside preprocessor text.
 *
 * @property name Called function name as written in the source.
 * @property nameStart Offset of the first character of [name].
 * @property argumentListStart Offset of the `(` opening the argument list.
 * @property argumentIndex Zero-based index of the argument the caret sits in.
 */
data class IsPreprocessorCallSite(
    val name: String,
    val nameStart: Int,
    val argumentListStart: Int,
    val argumentIndex: Int,
)

/**
 * Finds the innermost *named* call whose argument list encloses [offset] in [text], or `null` when the
 * offset is not inside a call.
 *
 * Works on plain text rather than on PSI because ISPP expressions are not part of the PSI tree — they are
 * parsed on demand by [IsPreprocessorExprParser]. Quoted strings are skipped, so a parenthesis or comma
 * inside a literal never opens a call or advances the argument index. Parentheses used purely for grouping
 * are tracked as unnamed frames, so `Copy((a), 1, 2)` still reports `Copy` with the correct index.
 */
fun findEnclosingCall(text: String, offset: Int): IsPreprocessorCallSite? {
    if (offset <= 0 || offset > text.length) return null

    // One frame per open parenthesis; `name` is null for a pure grouping parenthesis.
    class Frame(val name: String?, val nameStart: Int, val listStart: Int, var argumentIndex: Int = 0)

    val stack = ArrayDeque<Frame>()
    var i = 0
    while (i < offset) {
        when (val c = text[i]) {
            '"', '\'' -> {
                i++
                while (i < offset && text[i] != c) i++
            }

            '(' -> {
                val nameEnd = skipWhitespaceBackwards(text, i)
                val nameStart = identifierStart(text, nameEnd)
                val name = if (nameStart < nameEnd) text.substring(nameStart, nameEnd) else null
                stack.addLast(Frame(name, nameStart, i))
            }

            ')' -> stack.removeLastOrNull()

            ',' -> stack.lastOrNull()?.let { it.argumentIndex++ }

            else -> {}
        }
        i++
    }

    val frame = stack.lastOrNull { it.name != null } ?: return null
    return IsPreprocessorCallSite(frame.name!!, frame.nameStart, frame.listStart, frame.argumentIndex)
}

/** The offset after the last non-whitespace character before [end]. */
private fun skipWhitespaceBackwards(text: String, end: Int): Int {
    var i = end
    while (i > 0 && text[i - 1].isWhitespace()) i--
    return i
}

/** The start offset of the identifier ending at [end], or [end] itself when there is none. */
private fun identifierStart(text: String, end: Int): Int {
    var i = end
    while (i > 0 && (text[i - 1].isLetterOrDigit() || text[i - 1] == '_')) i--
    // An identifier must not start with a digit; otherwise it is a number, not a callee.
    if (i < end && text[i].isDigit()) return end
    return i
}
