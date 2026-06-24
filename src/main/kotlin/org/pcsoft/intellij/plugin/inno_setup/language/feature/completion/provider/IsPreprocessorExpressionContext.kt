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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.completion.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet

/**
 * Shared gating for completion providers that contribute to the expression part of a `#define` (the value
 * after the macro name). Both the reference/variable provider and the built-in function provider rely on it
 * so the "caret sits in the expression" detection and the word-prefix matcher stay in a single place.
 */
internal object IsPreprocessorExpressionContext {
    // There is already a complete name (with optional parameter list `(…)` or array index `[…]`) followed by
    // whitespace, i.e. the caret sits in the expression/value part — including the value of an array element
    // assignment `#define Name[0] <caret>`.
    private val EXPR_PREFIX =
        Regex(
            "^#\\s*define\\s+(?:(?:public|protected|private)\\s+)?[A-Za-z0-9_.\\-]+(?:\\([^)]*\\)|\\[[^\\]]*\\])?\\s+.*$",
            RegexOption.IGNORE_CASE,
        )

    // The caret sits inside the `[Index]`/`[Size]` of `#define Name[<caret>]` (element assignment) or
    // `#dim`/`#redim Name[<caret>]` (size) — that bracket content is itself an expression, so references are
    // completable. No `\s+` after the name (the `[` follows directly), which is why EXPR_PREFIX misses it.
    private val ARRAY_INDEX_PREFIX =
        Regex(
            "^#\\s*(?:define|dim|redim)\\s+(?:(?:public|protected|private)\\s+)?[A-Za-z0-9_.\\-]+\\[[^\\]]*$",
            RegexOption.IGNORE_CASE,
        )

    // The caret sits after the `[Size]` of a `#dim`/`#redim Name[Size] …` — i.e. in the inline initialiser
    // area, whether or not the opening `{` has been typed yet. Each element is an expression, so references
    // are completable there.
    private val DIM_INIT_PREFIX =
        Regex(
            "^#\\s*(?:dim|redim)\\s+(?:(?:public|protected|private)\\s+)?[A-Za-z0-9_.\\-]+\\[[^\\]]*\\]\\s*\\{?.*$",
            RegexOption.IGNORE_CASE,
        )

    // The caret sits in the expression argument of a `#pragma` sub-command that takes one (string/integer).
    private val PRAGMA_EXPR_PREFIX = Regex(
        "^#\\s*pragma\\s+(?:message|warning|error|verboselevel|include|inlinestart|inlineend|spansymbol)\\s+.*$",
        RegexOption.IGNORE_CASE,
    )

    // The caret sits in the condition of a `#if`/`#elif` (an expression). The `\s+` after the keyword keeps
    // `#ifdef`/`#ifndef`/`#ifexist`/`#ifnexist` (which take a name/filename, not an expression) out.
    private val IF_EXPR_PREFIX = Regex("^#\\s*(?:if|elif)\\s+.*$", RegexOption.IGNORE_CASE)

    // The caret sits in the filename argument of `#ifexist`/`#ifnexist` (a string expression).
    private val IFEXIST_EXPR_PREFIX = Regex("^#\\s*(?:ifexist|ifnexist)\\s+.*$", RegexOption.IGNORE_CASE)

    private val WORD_TAIL = Regex("[A-Za-z0-9_.\\-]*$")

    /**
     * The result set re-bound to the word being typed when the caret sits in a `#define` expression, or
     * `null` when the caret is not in an expression context (so the provider should contribute nothing).
     *
     * Identifiers inside a string literal of the expression (e.g. `#define X "abc <caret>"`) are plain text,
     * not references, so completion must not fire there. An odd number of quotes before the caret means the
     * caret is inside an open string.
     */
    fun adjustedResult(params: CompletionParameters, result: CompletionResultSet): CompletionResultSet? {
        val offset = params.offset
        val doc = params.editor.document
        val lineStart = doc.getLineStartOffset(doc.getLineNumber(offset))
        val linePrefix = doc.charsSequence.subSequence(lineStart, offset).toString()
        if (!EXPR_PREFIX.matches(linePrefix) &&
            !ARRAY_INDEX_PREFIX.matches(linePrefix) &&
            !DIM_INIT_PREFIX.matches(linePrefix) &&
            !PRAGMA_EXPR_PREFIX.matches(linePrefix) &&
            !IF_EXPR_PREFIX.matches(linePrefix) &&
            !IFEXIST_EXPR_PREFIX.matches(linePrefix)
        ) return null
        if (linePrefix.count { it == '"' } % 2 == 1) return null

        val typed = WORD_TAIL.find(linePrefix)?.value ?: ""
        return result.withPrefixMatcher(typed)
    }
}
