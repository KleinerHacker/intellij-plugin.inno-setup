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

/**
 * Infers the [IsPreprocessorExprType] of an [IsPreprocessorExprNode] tree and reports type violations.
 *
 * The rules follow the ISPP expression semantics (`topic_expressions`): `void` is compatible with both
 * integer and string operands; arithmetic/bitwise/logical operators require integers; `+` additionally
 * allows string concatenation; comparisons must not mix string and integer operands. Any operand of type
 * [IsPreprocessorExprType.ANY] (unresolved reference, macro parameter, unknown function result, `{…}`
 * constant) makes the surrounding operation permissive so that valid scripts never produce false errors.
 *
 * @param referenceType resolves a referenced identifier (macro / predefined variable) to its type.
 * @param functionReturnType resolves a called function name to its return type.
 */
class IsPreprocessorExprTypeInference(
    private val referenceType: (String) -> IsPreprocessorExprType = { IsPreprocessorExprType.ANY },
    private val functionReturnType: (String) -> IsPreprocessorExprType = { IsPreprocessorExprType.ANY },
) {

    private val errorList = mutableListOf<IsPreprocessorExprError>()

    /** Type violations collected during [infer]. */
    val errors: List<IsPreprocessorExprError> get() = errorList

    /** Infers the type of [node], appending any detected violations to [errors]. */
    fun infer(node: IsPreprocessorExprNode): IsPreprocessorExprType = when (node) {
        is IsPreprocessorExprIntLiteral -> IsPreprocessorExprType.INT
        is IsPreprocessorExprStrLiteral -> IsPreprocessorExprType.STR
        is IsPreprocessorExprConstant -> IsPreprocessorExprType.ANY
        is IsPreprocessorExprEmpty -> IsPreprocessorExprType.VOID
        is IsPreprocessorExprErrorNode -> IsPreprocessorExprType.ANY
        is IsPreprocessorExprReference -> referenceType(node.name)
        is IsPreprocessorExprParen -> infer(node.inner)
        is IsPreprocessorExprCall -> {
            node.arguments.forEach { infer(it) }
            functionReturnType(node.name)
        }
        is IsPreprocessorExprUnary -> unaryType(node)
        is IsPreprocessorExprBinary -> binaryType(node)
        is IsPreprocessorExprTernary -> ternaryType(node)
    }

    private fun unaryType(node: IsPreprocessorExprUnary): IsPreprocessorExprType {
        val operandType = infer(node.operand)
        if (!operandType.intCompatible) {
            errorList += IsPreprocessorExprError(
                node.operand.span,
                "Operator '${node.operator.symbol}' requires an integer operand, found string",
            )
        }
        return IsPreprocessorExprType.INT
    }

    private fun ternaryType(node: IsPreprocessorExprTernary): IsPreprocessorExprType {
        infer(node.condition)
        val whenTrue = infer(node.whenTrue)
        val whenFalse = infer(node.whenFalse)
        // ISPP is permissive about the branch types; only collapse to a concrete type when they agree.
        return if (whenTrue == whenFalse) whenTrue else IsPreprocessorExprType.ANY
    }

    private fun binaryType(node: IsPreprocessorExprBinary): IsPreprocessorExprType {
        val leftType = infer(node.left)
        val rightType = infer(node.right)
        val operator = node.operator

        if (operator.category == IsPreprocessorExprOperatorCategory.COMMA) return rightType

        if (leftType == IsPreprocessorExprType.ANY || rightType == IsPreprocessorExprType.ANY) {
            return when (operator) {
                IsPreprocessorExprBinaryOperator.PLUS -> IsPreprocessorExprType.ANY
                else -> IsPreprocessorExprType.INT
            }
        }

        @Suppress("KotlinConstantConditions")
        return when (operator.category) {
            IsPreprocessorExprOperatorCategory.ARITHMETIC ->
                if (operator == IsPreprocessorExprBinaryOperator.PLUS) plusType(node, leftType, rightType)
                else requireIntegers(node, leftType, rightType)

            IsPreprocessorExprOperatorCategory.BITWISE, IsPreprocessorExprOperatorCategory.LOGICAL ->
                requireIntegers(node, leftType, rightType)

            IsPreprocessorExprOperatorCategory.COMPARISON -> comparisonType(node, leftType, rightType)

            IsPreprocessorExprOperatorCategory.COMMA -> rightType
            IsPreprocessorExprOperatorCategory.TERNARY -> IsPreprocessorExprType.ANY
        }
    }

    /** `+`: integer addition or string concatenation; mixing string and integer is an error. */
    private fun plusType(
        node: IsPreprocessorExprBinary,
        leftType: IsPreprocessorExprType,
        rightType: IsPreprocessorExprType,
    ): IsPreprocessorExprType {
        val anyString = leftType == IsPreprocessorExprType.STR || rightType == IsPreprocessorExprType.STR
        if (!anyString) return IsPreprocessorExprType.INT
        return if (leftType.strCompatible && rightType.strCompatible) {
            IsPreprocessorExprType.STR
        } else {
            errorList += IsPreprocessorExprError(
                node.opSpan,
                "Operator '+' cannot combine a string operand with an integer operand",
            )
            IsPreprocessorExprType.ANY
        }
    }

    /** Arithmetic (except `+`), bitwise and logical operators require integer operands. */
    private fun requireIntegers(
        node: IsPreprocessorExprBinary,
        leftType: IsPreprocessorExprType,
        rightType: IsPreprocessorExprType,
    ): IsPreprocessorExprType {
        if (leftType == IsPreprocessorExprType.STR || rightType == IsPreprocessorExprType.STR) {
            errorList += IsPreprocessorExprError(
                node.opSpan,
                "Operator '${node.operator.symbol}' cannot be applied to a string operand",
            )
        }
        return IsPreprocessorExprType.INT
    }

    /** Comparisons may compare two integers or two strings, but not a string with an integer. */
    private fun comparisonType(
        node: IsPreprocessorExprBinary,
        leftType: IsPreprocessorExprType,
        rightType: IsPreprocessorExprType,
    ): IsPreprocessorExprType {
        val mixed = (leftType == IsPreprocessorExprType.STR && rightType == IsPreprocessorExprType.INT) ||
                (leftType == IsPreprocessorExprType.INT && rightType == IsPreprocessorExprType.STR)
        if (mixed) {
            errorList += IsPreprocessorExprError(
                node.opSpan,
                "Operator '${node.operator.symbol}' cannot compare a string operand with an integer operand",
            )
        }
        return IsPreprocessorExprType.INT
    }
}
