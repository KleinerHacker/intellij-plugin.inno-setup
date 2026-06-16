/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.expression

import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.expression.IsPreprocessorExprValue.IntValue
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.expression.IsPreprocessorExprValue.StrValue

/**
 * Statically evaluates an [IsPreprocessorExprNode] tree to an [IsPreprocessorExprValue] (integer or string),
 * or `null` when the value cannot be determined (function calls, `{…}` constants, unresolved references,
 * version-style numbers, division by zero, type-mismatched operands).
 *
 * Literal text is read from [text] because the AST nodes only carry spans. References are resolved through
 * [referenceValue] and function-like macro calls through [callValue] (it receives the evaluated argument
 * values, each `null` when an argument is itself not computable); anything they cannot resolve makes the
 * surrounding expression non-computable.
 */
class IsPreprocessorExprEvaluator(
    private val text: String,
    private val referenceValue: (String) -> IsPreprocessorExprValue? = { null },
    private val callValue: (String, List<IsPreprocessorExprValue?>) -> IsPreprocessorExprValue? = { _, _ -> null },
) {

    /** Evaluates [node], or returns `null` when it has no statically computable value. */
    fun evaluate(node: IsPreprocessorExprNode): IsPreprocessorExprValue? = when (node) {
        is IsPreprocessorExprIntLiteral -> literal(node).toLongOrNull()?.let { IntValue(it) }
        is IsPreprocessorExprStrLiteral -> StrValue(unquote(literal(node)))
        is IsPreprocessorExprReference -> referenceValue(node.name)
        is IsPreprocessorExprCall -> callValue(node.name, node.arguments.map { evaluate(it) })
        is IsPreprocessorExprParen -> evaluate(node.inner)
        is IsPreprocessorExprUnary -> unary(node)
        is IsPreprocessorExprBinary -> binary(node)
        is IsPreprocessorExprTernary -> ternary(node)
        is IsPreprocessorExprConstant,
        is IsPreprocessorExprEmpty,
        is IsPreprocessorExprErrorNode -> null
    }

    private fun literal(node: IsPreprocessorExprNode): String = text.substring(node.span.start, node.span.end)

    /** Strips the surrounding quotes of a string literal and unescapes doubled quotes. */
    private fun unquote(raw: String): String {
        if (raw.length < 2) return raw
        val q = raw.first()
        val end = if (raw.last() == q) raw.length - 1 else raw.length
        return raw.substring(1, end).replace("$q$q", "$q")
    }

    private fun unary(node: IsPreprocessorExprUnary): IsPreprocessorExprValue? {
        val v = (evaluate(node.operand) as? IntValue)?.value ?: return null
        return when (node.operator) {
            IsPreprocessorExprUnaryOperator.PLUS -> IntValue(v)
            IsPreprocessorExprUnaryOperator.MINUS -> IntValue(-v)
            IsPreprocessorExprUnaryOperator.NOT -> bool(v == 0L)
            IsPreprocessorExprUnaryOperator.BIT_NOT -> IntValue(v.inv())
        }
    }

    private fun ternary(node: IsPreprocessorExprTernary): IsPreprocessorExprValue? {
        val cond = (evaluate(node.condition) as? IntValue)?.value ?: return null
        return evaluate(if (cond != 0L) node.whenTrue else node.whenFalse)
    }

    private fun binary(node: IsPreprocessorExprBinary): IsPreprocessorExprValue? {
        val left = evaluate(node.left) ?: return null
        val right = evaluate(node.right) ?: return null
        val op = node.operator

        // String concatenation: `+` between two strings.
        if (op == IsPreprocessorExprBinaryOperator.PLUS && (left is StrValue || right is StrValue)) {
            return if (left is StrValue && right is StrValue) StrValue(left.value + right.value) else null
        }

        val a = (left as? IntValue)?.value ?: return null
        val b = (right as? IntValue)?.value ?: return null
        return when (op) {
            IsPreprocessorExprBinaryOperator.PLUS -> IntValue(a + b)
            IsPreprocessorExprBinaryOperator.MINUS -> IntValue(a - b)
            IsPreprocessorExprBinaryOperator.MUL -> IntValue(a * b)
            IsPreprocessorExprBinaryOperator.DIV -> if (b == 0L) null else IntValue(a / b)
            IsPreprocessorExprBinaryOperator.MOD -> if (b == 0L) null else IntValue(a % b)
            IsPreprocessorExprBinaryOperator.BIT_AND -> IntValue(a and b)
            IsPreprocessorExprBinaryOperator.BIT_OR -> IntValue(a or b)
            IsPreprocessorExprBinaryOperator.BIT_XOR -> IntValue(a xor b)
            IsPreprocessorExprBinaryOperator.SHL -> IntValue(a shl b.toInt())
            IsPreprocessorExprBinaryOperator.SHR -> IntValue(a shr b.toInt())
            IsPreprocessorExprBinaryOperator.EQ -> bool(a == b)
            IsPreprocessorExprBinaryOperator.NEQ -> bool(a != b)
            IsPreprocessorExprBinaryOperator.LT -> bool(a < b)
            IsPreprocessorExprBinaryOperator.GT -> bool(a > b)
            IsPreprocessorExprBinaryOperator.LE -> bool(a <= b)
            IsPreprocessorExprBinaryOperator.GE -> bool(a >= b)
            IsPreprocessorExprBinaryOperator.LOGICAL_AND -> bool(a != 0L && b != 0L)
            IsPreprocessorExprBinaryOperator.LOGICAL_OR -> bool(a != 0L || b != 0L)
            IsPreprocessorExprBinaryOperator.COMMA -> right
        }
    }

    private fun bool(b: Boolean) = IntValue(if (b) 1L else 0L)
}
