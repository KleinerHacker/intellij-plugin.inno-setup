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
 * Infers the [IsPreprocessorExprType] of an [IsPreprocessorExprNode] tree and reports type violations.
 *
 * The rules follow the ISPP expression semantics (`topic_expressions`): `void` is compatible with both
 * integer and string operands; arithmetic/bitwise/logical operators require integers; `+` additionally
 * allows string concatenation; comparisons must not mix string and integer operands. Any operand of type
 * [IsPreprocessorExprType.ANY] (unresolved reference, macro parameter, unknown function result, `{…}`
 * constant) makes the surrounding operation permissive so that valid scripts never produce false errors.
 *
 * @param referenceType resolves a referenced identifier (macro / predefined variable) to its type.
 * @param functionCallType resolves a call to its result type from the callee name and the inferred argument
 *   types. For a user function-like macro this is the type of its body with the parameters bound to the
 *   argument types (so `#define func(x) "abc" + x` called as `func("x")` yields `str`); for a built-in it is
 *   the spec return type.
 * @param functionMacroSignature the signature of a function-like macro (`#define f(int x, y = 1) …`), or
 *   `null` if the name is not one. Such a macro must be referenced as a call `f(…)`; the call is checked
 *   like a built-in call — argument count between the mandatory and the declared parameter count, argument
 *   types against the declared (or probed) parameter types, and a by-reference parameter (`*x`) requiring a
 *   bare macro name. A parameter of type `ANY`/`VOID` accepts any argument.
 * @param builtinSignature the structured signature of a built-in ISPP function, or `null` if the name is not
 *   one. A call to a built-in is checked against it: argument count (required vs. optional parameters),
 *   argument types, by-reference parameters (which need a bare macro name) and un-evaluated `Ident`/`Array`
 *   parameters (`Defined(X)`, `DimOf(A)`), whose argument is a symbol name rather than a value.
 *
 * A call to a name that is neither a built-in nor a macro is *not* reported here — the annotator reports it
 * as an unknown function, because only it knows the symbols of the whole host file.
 */
class IsPreprocessorExprTypeInference(
    private val referenceType: (String) -> IsPreprocessorExprType = { IsPreprocessorExprType.ANY },
    private val functionCallType: (String, List<IsPreprocessorExprType>) -> IsPreprocessorExprType =
        { _, _ -> IsPreprocessorExprType.ANY },
    private val functionMacroSignature: (String) -> IsPreprocessorBuiltinSignature? = { null },
    private val isArray: (String) -> Boolean = { false },
    private val builtinSignature: (String) -> IsPreprocessorBuiltinSignature? = { null },
) {

    private companion object {
        /** Operator categories whose operands must be integers (a string operand is always an error). */
        val STRING_FORBIDDEN_CATEGORIES = setOf(
            IsPreprocessorExprOperatorCategory.ARITHMETIC,
            IsPreprocessorExprOperatorCategory.BITWISE,
            IsPreprocessorExprOperatorCategory.LOGICAL,
        )
    }

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
        is IsPreprocessorExprReference -> referenceTypeChecked(node)
        is IsPreprocessorExprIndex -> indexType(node)
        is IsPreprocessorExprParen -> infer(node.inner)
        is IsPreprocessorExprCall -> callType(node)
        is IsPreprocessorExprUnary -> unaryType(node)
        is IsPreprocessorExprBinary -> binaryType(node)
        is IsPreprocessorExprTernary -> ternaryType(node)
    }

    /**
     * Type of a bare identifier reference. A function-like macro (`#define f(x) …`) must be invoked as a
     * call `f(…)`; using its name without an argument list is an error.
     */
    private fun referenceTypeChecked(node: IsPreprocessorExprReference): IsPreprocessorExprType {
        if (functionMacroSignature(node.name) != null) {
            errorList += IsPreprocessorExprError(
                node.span,
                "Function-like macro '${node.name}' must be called with an argument list",
            )
        }
        if (isArray(node.name)) {
            errorList += IsPreprocessorExprError(
                node.span,
                "Array '${node.name}' must be indexed with '[…]'",
            )
        }
        return referenceType(node.name)
    }

    /**
     * Type of an array element access `name[index]`. The base name must be an array (else an error) and the
     * index must be integer-compatible. Array elements are heterogeneous in ISPP (each can hold any type), so
     * the element type is [IsPreprocessorExprType.ANY]; static out-of-bounds checking lives in the annotator,
     * which has the source text.
     */
    private fun indexType(node: IsPreprocessorExprIndex): IsPreprocessorExprType {
        val indexType = infer(node.index)
        checkNotVoidCall(node.index)
        if (!indexType.intCompatible) {
            errorList += IsPreprocessorExprError(node.index.span, "Array index must be an integer")
        }
        if (!isArray(node.name)) {
            errorList += IsPreprocessorExprError(node.nameSpan, "'${node.name}' is not an array")
        }
        return IsPreprocessorExprType.ANY
    }

    /**
     * Type of a function call. The callee is either a function-like macro of the file or a built-in of the
     * ISPP specification; both are checked against their parameter list:
     * argument count and per-argument type. Arguments in an un-evaluated `Ident`/`Array` parameter position
     * of a built-in are validated as symbol names instead of being inferred as values.
     */
    private fun callType(node: IsPreprocessorExprCall): IsPreprocessorExprType {
        val macroSignature = functionMacroSignature(node.name)
        val signature = macroSignature ?: builtinSignature(node.name)

        val argTypes = node.arguments.mapIndexed { index, argument ->
            val parameter = signature?.parameters?.getOrNull(index)
            if (parameter != null && parameter.kind.isSymbolParameter) {
                checkSymbolArgument(node, index, argument, parameter)
                IsPreprocessorExprType.ANY
            } else {
                val type = infer(argument)
                checkNotVoidCall(argument)
                type
            }
        }

        if (macroSignature != null) checkMacroCall(node, macroSignature, argTypes)
        else if (signature != null) checkBuiltinCall(node, signature, argTypes)

        return functionCallType(node.name, argTypes)
    }

    /** Whether the parameter kind denotes an un-evaluated symbol name rather than a value. */
    private val IsPreprocessorBuiltinParameterKind.isSymbolParameter: Boolean
        get() = this == IsPreprocessorBuiltinParameterKind.IDENT ||
                this == IsPreprocessorBuiltinParameterKind.ARRAY

    /**
     * Checks a call to a function-like macro against its declared parameter list: the argument count must lie
     * between the mandatory count (parameters without a default value) and the declared parameter count, every
     * argument must be type-compatible with its parameter, and a by-reference parameter (`*x`) needs a bare
     * macro name it can write back into.
     */
    private fun checkMacroCall(
        node: IsPreprocessorExprCall,
        signature: IsPreprocessorBuiltinSignature,
        argTypes: List<IsPreprocessorExprType>,
    ) {
        if (node.arguments.size < signature.requiredCount || node.arguments.size > signature.maxCount) {
            errorList += IsPreprocessorExprError(
                node.nameSpan,
                "Function-like macro '${node.name}' expects ${signature.arityDescription}, " +
                        "but got ${node.arguments.size}",
            )
            return
        }
        node.arguments.forEachIndexed { i, arg ->
            val parameter = signature.parameters[i]

            if (parameter.byRef && bareReferenceName(arg) == null) {
                errorList += IsPreprocessorExprError(
                    arg.span,
                    "Argument ${i + 1} ('${parameter.name}') of macro '${node.name}' is passed by reference " +
                            "and must be a macro name",
                )
                return@forEachIndexed
            }

            val expected = when (parameter.kind) {
                IsPreprocessorBuiltinParameterKind.INT -> IsPreprocessorExprType.INT
                IsPreprocessorBuiltinParameterKind.STR -> IsPreprocessorExprType.STR
                else -> IsPreprocessorExprType.ANY
            }
            if (!argumentCompatible(expected, argTypes[i])) {
                errorList += IsPreprocessorExprError(
                    arg.span,
                    "Argument ${i + 1} ('${parameter.name}') of macro '${node.name}' must be " +
                            "${expected.name.lowercase()}, but got ${argTypes[i].name.lowercase()}",
                )
            }
        }
    }

    /**
     * Checks a call to a built-in function against its specification signature: the number of arguments must
     * lie between the required and the maximum count, every value argument must be type-compatible with its
     * parameter, and a by-reference parameter needs a bare macro name it can write back into.
     */
    private fun checkBuiltinCall(
        node: IsPreprocessorExprCall,
        signature: IsPreprocessorBuiltinSignature,
        argTypes: List<IsPreprocessorExprType>,
    ) {
        if (node.arguments.size < signature.requiredCount || node.arguments.size > signature.maxCount) {
            errorList += IsPreprocessorExprError(
                node.nameSpan,
                "Function '${signature.name}' expects ${signature.arityDescription}, " +
                        "but got ${node.arguments.size}",
            )
            return
        }
        node.arguments.forEachIndexed { i, arg ->
            val parameter = signature.parameters[i]
            if (parameter.kind.isSymbolParameter) return@forEachIndexed // already validated as a symbol name

            if (parameter.byRef && bareReferenceName(arg) == null) {
                errorList += IsPreprocessorExprError(
                    arg.span,
                    "Argument ${i + 1} of '${signature.name}' is passed by reference " +
                            "and must be a macro name",
                )
                return@forEachIndexed
            }

            val expected = when (parameter.kind) {
                IsPreprocessorBuiltinParameterKind.INT -> IsPreprocessorExprType.INT
                IsPreprocessorBuiltinParameterKind.STR -> IsPreprocessorExprType.STR
                else -> IsPreprocessorExprType.ANY
            }
            if (!argumentCompatible(expected, argTypes[i])) {
                errorList += IsPreprocessorExprError(
                    arg.span,
                    "Argument ${i + 1} ('${parameter.name}') of '${signature.name}' must be " +
                            "${expected.name.lowercase()}, but got ${argTypes[i].name.lowercase()}",
                )
            }
        }
    }

    /**
     * Checks an argument in an un-evaluated `Ident`/`Array` parameter position: it must be a bare identifier
     * (ISPP passes the *name*, not a value), and for an `Array` parameter that name must be a declared array.
     */
    private fun checkSymbolArgument(
        node: IsPreprocessorExprCall,
        index: Int,
        argument: IsPreprocessorExprNode,
        parameter: IsPreprocessorBuiltinParameter,
    ) {
        val name = bareReferenceName(argument)
        if (name == null) {
            errorList += IsPreprocessorExprError(
                argument.span,
                "Argument ${index + 1} of '${node.name}' must be a macro name",
            )
            return
        }
        if (parameter.kind == IsPreprocessorBuiltinParameterKind.ARRAY && !isArray(name)) {
            errorList += IsPreprocessorExprError(argument.span, "'$name' is not an array")
        }
    }

    /** The identifier of a bare (possibly parenthesised) reference node, or `null` for any other node. */
    private fun bareReferenceName(node: IsPreprocessorExprNode): String? = when (node) {
        is IsPreprocessorExprReference -> node.name
        is IsPreprocessorExprParen -> bareReferenceName(node.inner)
        else -> null
    }

    /**
     * Reports [node] when it is a call to a built-in that returns nothing (`Error`, `Message`, `FileClose`, …)
     * but is used where a value is required.
     */
    private fun checkNotVoidCall(node: IsPreprocessorExprNode) {
        val call = when (node) {
            is IsPreprocessorExprCall -> node
            is IsPreprocessorExprParen -> return checkNotVoidCall(node.inner)
            else -> return
        }
        val signature = builtinSignature(call.name) ?: return
        if (signature.returnType != IsPreprocessorExprType.VOID) return
        errorList += IsPreprocessorExprError(
            call.span,
            "Function '${signature.name}' returns no value and cannot be used in an expression",
        )
    }

    /** Whether an argument of type [actual] is acceptable for a parameter of (probable) type [expected]. */
    private fun argumentCompatible(
        expected: IsPreprocessorExprType,
        actual: IsPreprocessorExprType,
    ): Boolean = when (expected) {
        IsPreprocessorExprType.INT -> actual.intCompatible
        IsPreprocessorExprType.STR -> actual.strCompatible
        IsPreprocessorExprType.VOID, IsPreprocessorExprType.ANY -> true
    }

    private fun unaryType(node: IsPreprocessorExprUnary): IsPreprocessorExprType {
        val operandType = infer(node.operand)
        checkNotVoidCall(node.operand)
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
        checkNotVoidCall(node.condition)
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

        checkNotVoidCall(node.left)
        checkNotVoidCall(node.right)

        if (leftType == IsPreprocessorExprType.ANY || rightType == IsPreprocessorExprType.ANY) {
            // `+` stays permissive (concatenation with an unknown value may be valid). For every other
            // arithmetic/bitwise/logical operator a *concrete* string operand is always illegal, even when
            // the other side is unresolved — e.g. `"abc" * x` where `x` is a macro parameter.
            if (operator != IsPreprocessorExprBinaryOperator.PLUS &&
                operator.category in STRING_FORBIDDEN_CATEGORIES &&
                (leftType == IsPreprocessorExprType.STR || rightType == IsPreprocessorExprType.STR)
            ) {
                errorList += IsPreprocessorExprError(
                    node.opSpan,
                    "Operator '${operator.symbol}' cannot be applied to a string operand",
                )
            }
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

            // An assignment evaluates to the assigned value, so its type is the type of the right operand.
            // ISPP variables are untyped, so any value may be assigned to any target — nothing to check.
            IsPreprocessorExprOperatorCategory.ASSIGNMENT -> rightType
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
