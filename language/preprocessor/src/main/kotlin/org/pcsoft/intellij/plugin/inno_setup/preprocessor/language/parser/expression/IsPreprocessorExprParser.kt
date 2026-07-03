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

/** Result of parsing an ISPP expression: its AST plus any structural (syntax) errors found. */
data class IsPreprocessorExprParseResult(
    val ast: IsPreprocessorExprNode,
    val errors: List<IsPreprocessorExprError>,
)

/**
 * A fault-tolerant recursive-descent / precedence-climbing parser for ISPP expressions.
 *
 * It never throws on malformed input: instead it records [IsPreprocessorExprError]s anchored at the precise
 * offending token (a missing operator points at the unexpected operand, a missing operand at the position
 * where one was expected, an unbalanced parenthesis at the relevant bracket).
 */
class IsPreprocessorExprParser private constructor(
    private val tokens: List<IsPreprocessorExprToken>,
    private val length: Int,
) {

    private var pos = 0
    private val errors = mutableListOf<IsPreprocessorExprError>()

    companion object {
        /** Tokenizes and parses [text] in one step. */
        fun parse(text: String): IsPreprocessorExprParseResult {
            val tokens = IsPreprocessorExprTokenizer.tokenize(text)
            return IsPreprocessorExprParser(tokens, text.length).parse()
        }

        /** Parses an already-tokenized [tokens] sequence spanning `[0, length)`. */
        fun parse(tokens: List<IsPreprocessorExprToken>, length: Int): IsPreprocessorExprParseResult =
            IsPreprocessorExprParser(tokens, length).parse()
    }

    private fun parse(): IsPreprocessorExprParseResult {
        if (tokens.isEmpty()) {
            return IsPreprocessorExprParseResult(
                IsPreprocessorExprEmpty(IsPreprocessorExprSpan(0, length)),
                emptyList()
            )
        }

        val node = parseExpression(IsPreprocessorExprBinaryOperator.MIN_PRECEDENCE)
        peek()?.let { trailing ->
            // A second operand with no operator in between (e.g. `5 6`): flag the unexpected token.
            errors += IsPreprocessorExprError(trailing.span, "Operator expected before '${trailing.text}'")
        }
        return IsPreprocessorExprParseResult(node, errors.toList())
    }

    private fun parseExpression(minPrecedence: Int): IsPreprocessorExprNode {
        var left = parseUnary()
        while (true) {
            val token = peek() ?: break

            if (token.type == IsPreprocessorExprTokenType.QUESTION &&
                minPrecedence <= IsPreprocessorExprBinaryOperator.TERNARY_PRECEDENCE
            ) {
                advance()
                val whenTrue = parseExpression(IsPreprocessorExprBinaryOperator.MIN_PRECEDENCE)
                val colon = peek()
                if (colon?.type == IsPreprocessorExprTokenType.COLON) {
                    advance()
                } else {
                    errors += IsPreprocessorExprError(token.span, "Incomplete ternary operator: ':' expected")
                }
                val whenFalse = parseExpression(IsPreprocessorExprBinaryOperator.TERNARY_PRECEDENCE)
                left = IsPreprocessorExprTernary(
                    left, whenTrue, whenFalse, IsPreprocessorExprSpan(left.span.start, whenFalse.span.end),
                )
                continue
            }

            val operator = binaryOperatorAt(token) ?: break
            if (operator.precedence < minPrecedence) break
            advance()
            val right = parseExpression(operator.precedence + 1)
            left = IsPreprocessorExprBinary(
                operator, token.span, left, right, IsPreprocessorExprSpan(left.span.start, right.span.end),
            )
        }
        return left
    }

    private fun parseUnary(): IsPreprocessorExprNode {
        val token = peek()
        if (token != null && token.type == IsPreprocessorExprTokenType.OPERATOR) {
            val unary = IsPreprocessorExprUnaryOperator.forSymbol(token.text)
            if (unary != null) {
                advance()
                val operand = parseUnary()
                return IsPreprocessorExprUnary(
                    unary, token.span, operand, IsPreprocessorExprSpan(token.start, operand.span.end),
                )
            }
        }
        return parsePrimary()
    }

    private fun parsePrimary(): IsPreprocessorExprNode {
        val token = peek()
        if (token == null) {
            val span = IsPreprocessorExprSpan(length, length)
            errors += IsPreprocessorExprError(span, "Operand expected")
            return IsPreprocessorExprErrorNode(span)
        }
        return when (token.type) {
            IsPreprocessorExprTokenType.NUMBER -> {
                advance(); IsPreprocessorExprIntLiteral(token.span)
            }

            IsPreprocessorExprTokenType.STRING -> {
                advance(); IsPreprocessorExprStrLiteral(token.span)
            }

            IsPreprocessorExprTokenType.CONSTANT -> {
                advance(); IsPreprocessorExprConstant(token.span)
            }

            IsPreprocessorExprTokenType.IDENT -> {
                advance()
                when (peek()?.type) {
                    IsPreprocessorExprTokenType.LPAREN -> parseCall(token)
                    IsPreprocessorExprTokenType.LBRACKET -> parseIndex(token)
                    else -> IsPreprocessorExprReference(token.text, token.span)
                }
            }

            IsPreprocessorExprTokenType.LPAREN -> parseParen(token)
            else -> {
                advance()
                errors += IsPreprocessorExprError(token.span, "Operand expected")
                IsPreprocessorExprErrorNode(token.span)
            }
        }
    }

    private fun parseParen(open: IsPreprocessorExprToken): IsPreprocessorExprNode {
        advance() // consume '('
        val inner = parseExpression(IsPreprocessorExprBinaryOperator.MIN_PRECEDENCE)
        val close = peek()
        return if (close?.type == IsPreprocessorExprTokenType.RPAREN) {
            advance()
            IsPreprocessorExprParen(inner, IsPreprocessorExprSpan(open.start, close.end))
        } else {
            errors += IsPreprocessorExprError(open.span, "Unbalanced parenthesis: ')' expected")
            IsPreprocessorExprParen(inner, IsPreprocessorExprSpan(open.start, inner.span.end))
        }
    }

    private fun parseCall(name: IsPreprocessorExprToken): IsPreprocessorExprNode {
        advance() // consume '('
        val arguments = mutableListOf<IsPreprocessorExprNode>()
        if (peek()?.type != IsPreprocessorExprTokenType.RPAREN) {
            while (true) {
                arguments += parseExpression(IsPreprocessorExprBinaryOperator.TERNARY_PRECEDENCE) // stop at the comma
                if (peek()?.type == IsPreprocessorExprTokenType.COMMA) {
                    advance()
                } else {
                    break
                }
            }
        }
        val close = peek()
        val end = if (close?.type == IsPreprocessorExprTokenType.RPAREN) {
            advance()
            close.end
        } else {
            errors += IsPreprocessorExprError(name.span, "Unbalanced parenthesis: ')' expected")
            arguments.lastOrNull()?.span?.end ?: name.end
        }
        return IsPreprocessorExprCall(name.text, name.span, arguments, IsPreprocessorExprSpan(name.start, end))
    }

    private fun parseIndex(name: IsPreprocessorExprToken): IsPreprocessorExprNode {
        advance() // consume '['
        val index = parseExpression(IsPreprocessorExprBinaryOperator.MIN_PRECEDENCE)
        val close = peek()
        val end = if (close?.type == IsPreprocessorExprTokenType.RBRACKET) {
            advance()
            close.end
        } else {
            errors += IsPreprocessorExprError(name.span, "Unbalanced bracket: ']' expected")
            index.span.end
        }
        return IsPreprocessorExprIndex(name.text, name.span, index, IsPreprocessorExprSpan(name.start, end))
    }

    private fun binaryOperatorAt(token: IsPreprocessorExprToken): IsPreprocessorExprBinaryOperator? =
        when (token.type) {
            IsPreprocessorExprTokenType.OPERATOR -> IsPreprocessorExprBinaryOperator.forSymbol(token.text)
            IsPreprocessorExprTokenType.COMMA -> IsPreprocessorExprBinaryOperator.COMMA
            else -> null
        }

    private fun peek(): IsPreprocessorExprToken? = tokens.getOrNull(pos)

    private fun advance() {
        pos++
    }
}
