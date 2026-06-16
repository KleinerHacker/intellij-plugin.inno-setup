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

/** Abstract syntax tree node of an ISPP expression. Every node carries its source [span]. */
sealed class IsPreprocessorExprNode {
    abstract val span: IsPreprocessorExprSpan
}

/** An integer literal such as `100`. */
data class IsPreprocessorExprIntLiteral(override val span: IsPreprocessorExprSpan) : IsPreprocessorExprNode()

/** A string literal such as `"."`. */
data class IsPreprocessorExprStrLiteral(override val span: IsPreprocessorExprSpan) : IsPreprocessorExprNode()

/** A `{…}` constant — its type is not statically known, so it is treated as [IsPreprocessorExprType.ANY]. */
data class IsPreprocessorExprConstant(override val span: IsPreprocessorExprSpan) : IsPreprocessorExprNode()

/** An empty expression (a `#define` without a value) — type [IsPreprocessorExprType.VOID]. */
data class IsPreprocessorExprEmpty(override val span: IsPreprocessorExprSpan) : IsPreprocessorExprNode()

/** A reference to another macro, a predefined variable or a macro parameter. */
data class IsPreprocessorExprReference(
    val name: String,
    override val span: IsPreprocessorExprSpan,
) : IsPreprocessorExprNode()

/** A built-in or user function call such as `Str(Major)`. */
data class IsPreprocessorExprCall(
    val name: String,
    val nameSpan: IsPreprocessorExprSpan,
    val arguments: List<IsPreprocessorExprNode>,
    override val span: IsPreprocessorExprSpan,
) : IsPreprocessorExprNode()

/** A prefix unary operation such as `-x`. [opSpan] is the operator token. */
data class IsPreprocessorExprUnary(
    val operator: IsPreprocessorExprUnaryOperator,
    val opSpan: IsPreprocessorExprSpan,
    val operand: IsPreprocessorExprNode,
    override val span: IsPreprocessorExprSpan,
) : IsPreprocessorExprNode()

/** A binary operation such as `a * b`. [opSpan] is the operator token used for error highlighting. */
data class IsPreprocessorExprBinary(
    val operator: IsPreprocessorExprBinaryOperator,
    val opSpan: IsPreprocessorExprSpan,
    val left: IsPreprocessorExprNode,
    val right: IsPreprocessorExprNode,
    override val span: IsPreprocessorExprSpan,
) : IsPreprocessorExprNode()

/** A ternary conditional `cond ? whenTrue : whenFalse`. */
data class IsPreprocessorExprTernary(
    val condition: IsPreprocessorExprNode,
    val whenTrue: IsPreprocessorExprNode,
    val whenFalse: IsPreprocessorExprNode,
    override val span: IsPreprocessorExprSpan,
) : IsPreprocessorExprNode()

/** A parenthesised expression `( inner )`. */
data class IsPreprocessorExprParen(
    val inner: IsPreprocessorExprNode,
    override val span: IsPreprocessorExprSpan,
) : IsPreprocessorExprNode()

/** A placeholder for a missing operand; never produced for valid input. */
data class IsPreprocessorExprErrorNode(override val span: IsPreprocessorExprSpan) : IsPreprocessorExprNode()

/** A problem detected during analysis, anchored at the precise offending [span]. */
data class IsPreprocessorExprError(val span: IsPreprocessorExprSpan, val message: String)
