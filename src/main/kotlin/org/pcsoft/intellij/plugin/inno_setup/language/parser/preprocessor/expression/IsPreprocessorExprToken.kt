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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.expression

/** Kinds of token the [IsPreprocessorExprTokenizer] produces. */
enum class IsPreprocessorExprTokenType {
    NUMBER,
    STRING,
    IDENT,
    CONSTANT,
    OPERATOR,
    LPAREN,
    RPAREN,
    COMMA,
    QUESTION,
    COLON,
}

/** A half-open `[start, end)` range within the analysed expression string. */
data class IsPreprocessorExprSpan(val start: Int, val end: Int)

/**
 * A single lexical token of an ISPP expression.
 *
 * @property start offset of the token start within the analysed expression string (inclusive).
 * @property end offset of the token end within the analysed expression string (exclusive).
 */
data class IsPreprocessorExprToken(
    val type: IsPreprocessorExprTokenType,
    val text: String,
    val start: Int,
    val end: Int,
) {
    /** Source span of this token. */
    val span: IsPreprocessorExprSpan get() = IsPreprocessorExprSpan(start, end)
}
