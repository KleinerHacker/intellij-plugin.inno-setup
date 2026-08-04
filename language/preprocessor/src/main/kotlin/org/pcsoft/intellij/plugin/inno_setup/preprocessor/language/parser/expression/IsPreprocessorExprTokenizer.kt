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
 * Splits an ISPP expression string into [IsPreprocessorExprToken]s.
 *
 * The tokenizer works directly on the raw expression text (independent of the JFlex section lexer) so the
 * analysis is not affected by the host lexer's quirks (e.g. `-` being part of identifiers, operators being
 * lumped into `VALUE_CHAR`). Every token carries its exact offsets so that errors can point at the precise
 * offending character.
 */
object IsPreprocessorExprTokenizer {

    /** Tokenizes [text]; unknown characters are skipped (the parser reports structural problems). */
    fun tokenize(text: String): List<IsPreprocessorExprToken> {
        val tokens = mutableListOf<IsPreprocessorExprToken>()
        var i = 0
        while (i < text.length) {
            val c = text[i]
            when {
                c.isWhitespace() -> i++

                c == '"' || c == '\'' -> {
                    val end = readString(text, i, c)
                    tokens += token(IsPreprocessorExprTokenType.STRING, text, i, end)
                    i = end
                }

                c == '{' -> {
                    val end = readConstant(text, i)
                    tokens += token(IsPreprocessorExprTokenType.CONSTANT, text, i, end)
                    i = end
                }

                c.isDigit() -> {
                    val end = readNumber(text, i)
                    tokens += token(IsPreprocessorExprTokenType.NUMBER, text, i, end)
                    i = end
                }

                c.isLetter() || c == '_' -> {
                    val end = readIdent(text, i)
                    tokens += token(IsPreprocessorExprTokenType.IDENT, text, i, end)
                    i = end
                }

                c == '(' -> {
                    tokens += token(IsPreprocessorExprTokenType.LPAREN, text, i, i + 1); i++
                }

                c == ')' -> {
                    tokens += token(IsPreprocessorExprTokenType.RPAREN, text, i, i + 1); i++
                }

                c == '[' -> {
                    tokens += token(IsPreprocessorExprTokenType.LBRACKET, text, i, i + 1); i++
                }

                c == ']' -> {
                    tokens += token(IsPreprocessorExprTokenType.RBRACKET, text, i, i + 1); i++
                }

                c == ',' -> {
                    tokens += token(IsPreprocessorExprTokenType.COMMA, text, i, i + 1); i++
                }

                c == '?' -> {
                    tokens += token(IsPreprocessorExprTokenType.QUESTION, text, i, i + 1); i++
                }

                c == ':' -> {
                    tokens += token(IsPreprocessorExprTokenType.COLON, text, i, i + 1); i++
                }

                // A trailing backslash only continues the directive on the next physical line; it carries no
                // meaning for the expression itself and is therefore skipped like whitespace.
                c == '\\' -> i++

                else -> {
                    val op = matchOperator(text, i)
                    if (op != null) {
                        tokens += token(IsPreprocessorExprTokenType.OPERATOR, text, i, i + op.length)
                        i += op.length
                    } else {
                        // Unknown character: skip it; the parser surfaces the resulting structural issue.
                        i++
                    }
                }
            }
        }
        return tokens
    }

    private fun token(type: IsPreprocessorExprTokenType, text: String, start: Int, end: Int) =
        IsPreprocessorExprToken(type, text.substring(start, end), start, end)

    private fun matchOperator(text: String, start: Int): String? =
        IsPreprocessorExprOperators.SYMBOLS.firstOrNull { text.startsWith(it, start) }

    /** Reads a quoted string starting at [start] (the opening [quote]); doubled quotes are escapes. */
    private fun readString(text: String, start: Int, quote: Char): Int {
        var i = start + 1
        while (i < text.length) {
            if (text[i] == quote) {
                if (i + 1 < text.length && text[i + 1] == quote) {
                    i += 2 // doubled quote → literal quote
                } else {
                    return i + 1 // closing quote
                }
            } else {
                i++
            }
        }
        return i // unterminated string ends at end of input
    }

    /** Reads a `{…}` constant starting at [start]; supports nested braces. */
    private fun readConstant(text: String, start: Int): Int {
        var depth = 0
        var i = start
        while (i < text.length) {
            when (text[i]) {
                '{' -> depth++
                '}' -> {
                    depth--
                    if (depth == 0) return i + 1
                }
            }
            i++
        }
        return i
    }

    private fun readNumber(text: String, start: Int): Int {
        var i = start
        while (i < text.length && (text[i].isDigit() || text[i] == '.')) i++
        return i
    }

    private fun readIdent(text: String, start: Int): Int {
        var i = start
        while (i < text.length && (text[i].isLetterOrDigit() || text[i] == '_')) i++
        return i
    }
}
