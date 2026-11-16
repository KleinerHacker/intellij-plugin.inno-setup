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

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.feature.include.IsAnnotationSink
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.quickfix.RemoveExtraClosingBraceQuickFix

/**
 * Annotations around the escape sequences of a `#` directive that the lexer cannot supply (see
 * [IsEscapeSequences]):
 *
 * - colours the escapes inside **single-quoted** ISPP strings (`'…'`): the doubled quote `''` and the percent
 *   escapes. Double-quoted strings and `{{` are lexed into escape tokens, which the [IsPreprocessorAnnotator]
 *   colours itself.
 * - warns about `{{…}}` with two closing braces, with a quick fix
 *
 * A single quote that has no partner on the line is an apostrophe of free text (`#error Don't …`), not a
 * string, and is left alone.
 */
object IsPreprocessorEscapeAnnotations {

    /** Annotates [directive]; all ranges are in the coordinates of the directive's (injected) file. */
    fun annotate(directive: IsPreprocessorDirective, holder: IsAnnotationSink) {
        val text = directive.text
        val base = directive.textRange.startOffset
        singleQuotedEscapes(text).forEach { range ->
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(TextRange(base + range.first, base + range.last + 1))
                .textAttributes(IsPreprocessorSyntaxHighlighting.ESCAPE_SEQUENCE)
                .create()
        }
        IsEscapeSequences.DOUBLE_CLOSED_BRACE.findAll(text).forEach { match ->
            val extra = match.range.last
            holder.newAnnotation(HighlightSeverity.WARNING, IsEscapeSequences.DOUBLE_CLOSED_BRACE_MESSAGE)
                .range(TextRange(base + extra, base + extra + 1))
                .withFix(RemoveExtraClosingBraceQuickFix(directive, extra))
                .create()
        }
    }

    /**
     * The ranges of the escapes inside the single-quoted strings of [text]. A string outside of a
     * double-quoted string starts at a `'` and ends at the next single `'`; a doubled `''` inside it is an escape.
     */
    internal fun singleQuotedEscapes(text: String): List<IntRange> {
        val result = mutableListOf<IntRange>()
        var inDouble = false
        var i = 0
        while (i < text.length) {
            val c = text[i]
            when {
                c == '"' -> {
                    inDouble = !inDouble
                    i++
                }

                c == '\'' && !inDouble -> {
                    val end = readSingleQuoted(text, i, result)
                    i = if (end < 0) i + 1 else end
                }

                else -> i++
            }
        }
        return result
    }

    /**
     * Reads the single-quoted string that starts at [start], adds its escapes to [into] and returns the offset
     * behind the closing quote, or `-1` when the quote has no partner (an apostrophe).
     */
    private fun readSingleQuoted(text: String, start: Int, into: MutableList<IntRange>): Int {
        val found = mutableListOf<IntRange>()
        var i = start + 1
        while (i < text.length) {
            when {
                text[i] == '\'' && i + 1 < text.length && text[i + 1] == '\'' -> {
                    found += i..i + 1
                    i += 2
                }

                text[i] == '\'' -> {
                    into += found
                    return i + 1
                }

                // "{{" needs no handling here: the lexer reads it as an escape token wherever it stands.
                else -> {
                    val percent = IsEscapeSequences.PERCENT_ESCAPE.matchAt(text, i)
                    if (percent != null) {
                        found += percent.range
                        i = percent.range.last + 1
                    } else {
                        i++
                    }
                }
            }
        }
        return -1
    }
}
