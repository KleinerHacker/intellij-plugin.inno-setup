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
 * A `#define Name Expression` declaration as seen by the resolver, ordered by [order] (declaration order).
 */
data class IsPreprocessorExprDefineInfo(val name: String, val expression: String, val order: Int)

/**
 * A function-like macro `#define Name(params…) body` as seen by the resolver, ordered by [order].
 */
data class IsPreprocessorExprFunctionMacroInfo(
    val name: String,
    val parameters: List<String>,
    val body: String,
    val order: Int,
)

/**
 * Resolves the [IsPreprocessorExprType] of a referenced identifier by recursively inferring the type of the
 * `#define` it points at — so a type error like `str * int` is detected even when the operands are
 * themselves other `#define`s (e.g. `#define A "x"` / `#define B 5` / `#define C A * B`).
 *
 * Function-like macros are resolved the same way: a call's result type is the type of the macro body with the
 * parameters bound to the inferred argument types. So `#define func(x) "abc" + x` called as `func("x")` yields
 * `str`, which lets `#define myVar func("x") + intVar` flag the `str + int` mix.
 *
 * Two independent safeguards prevent infinite recursion:
 *  1. **Declaration order** — a reference only resolves to a `#define`/macro declared *before* the referencing
 *     line ([beforeOrder]). A well-formed script can therefore never form a reference ring, because every
 *     macro must already exist earlier to be referenced.
 *  2. **Cycle guard** — `visiting`/`visitingMacros` sets plus memoization break any residual cycle (e.g. a
 *     self-reference or an out-of-order script) by yielding [IsPreprocessorExprType.ANY].
 *
 * @param defines all simple (non function-like) `#define`s of the file.
 * @param functionMacros all function-like macros of the file.
 * @param variableType resolves a predefined variable name to its type, or `null` if it is not one.
 * @param builtinReturnType resolves a built-in function name to its return type.
 */
class IsPreprocessorExprTypeResolver(
    defines: List<IsPreprocessorExprDefineInfo>,
    functionMacros: List<IsPreprocessorExprFunctionMacroInfo> = emptyList(),
    private val variableType: (String) -> IsPreprocessorExprType? = { null },
    private val builtinReturnType: (String) -> IsPreprocessorExprType = { IsPreprocessorExprType.ANY },
) {

    private val defines: List<IsPreprocessorExprDefineInfo> = defines.sortedBy { it.order }
    private val functionMacros: List<IsPreprocessorExprFunctionMacroInfo> = functionMacros.sortedBy { it.order }
    private val cache = HashMap<String, IsPreprocessorExprType>()
    private val visiting = HashSet<String>()
    private val visitingMacros = HashSet<String>()

    /**
     * Type of the identifier [name] referenced from a line at position [beforeOrder].
     *
     * Predefined variables resolve to their declared type; otherwise the latest `#define` of that name
     * declared before [beforeOrder] is inferred recursively. Unknown identifiers yield
     * [IsPreprocessorExprType.ANY].
     */
    fun typeOfReference(name: String, beforeOrder: Int): IsPreprocessorExprType {
        variableType(name)?.let { return it }

        val key = name.lowercase()
        if (key in visiting) return IsPreprocessorExprType.ANY // residual cycle guard

        val target = defines
            .filter { it.order < beforeOrder && it.name.equals(name, ignoreCase = true) }
            .maxByOrNull { it.order }
            ?: return IsPreprocessorExprType.ANY

        val cacheKey = "$key@${target.order}"
        cache[cacheKey]?.let { return it }

        visiting += key
        val type = try {
            inferDefineType(target)
        } finally {
            visiting -= key
        }
        cache[cacheKey] = type
        return type
    }

    /** Declared parameter count of the latest function-like macro named [name], or `null` if there is none. */
    fun arityOf(name: String): Int? = latestMacro(name)?.parameters?.size

    private fun latestMacro(name: String): IsPreprocessorExprFunctionMacroInfo? =
        functionMacros.filter { it.name.equals(name, ignoreCase = true) }.maxByOrNull { it.order }

    /** Probable parameter types of [name] when it is a function-like macro, else `null`. */
    private fun macroParameterTypesOrNull(name: String): List<IsPreprocessorExprType>? =
        if (latestMacro(name) != null) probableMacroParameterTypes(name) else null

    /** Arity-shaped list of `ANY` for [name] — keeps the arity/bare-reference checks but disables type checks. */
    private fun anyParameterTypesOrNull(name: String): List<IsPreprocessorExprType>? =
        arityOf(name)?.let { count -> List(count) { IsPreprocessorExprType.ANY } }

    /**
     * Probable parameter types of the function-like macro [name], in order. A parameter's type is determined
     * by probing: if binding it to `str` makes the body ill-typed while `int` does not, it must be `int`
     * (and vice versa); otherwise it is [IsPreprocessorExprType.ANY] (unconstrained). Empty when [name] is not
     * a function-like macro.
     */
    fun probableMacroParameterTypes(name: String): List<IsPreprocessorExprType> {
        val macro = latestMacro(name) ?: return emptyList()
        return macro.parameters.indices.map { probeParameterType(macro, it) }
    }

    /**
     * Probable return type of the function-like macro [name]: the type of its body with the parameters bound
     * to their [probableMacroParameterTypes]. [IsPreprocessorExprType.ANY] when it cannot be determined.
     */
    fun probableMacroReturnType(name: String): IsPreprocessorExprType {
        val macro = latestMacro(name) ?: return IsPreprocessorExprType.ANY
        val paramTypes = probableMacroParameterTypes(name)
        val bound = macro.parameters.mapIndexed { i, p -> p to (paramTypes.getOrNull(i) ?: IsPreprocessorExprType.ANY) }
            .toMap()
        return inferMacroBodyType(macro, bound)
    }

    private fun probeParameterType(
        macro: IsPreprocessorExprFunctionMacroInfo,
        index: Int,
    ): IsPreprocessorExprType {
        val errorAsInt = bodyHasErrorWith(macro, index, IsPreprocessorExprType.INT)
        val errorAsStr = bodyHasErrorWith(macro, index, IsPreprocessorExprType.STR)
        return when {
            errorAsStr && !errorAsInt -> IsPreprocessorExprType.INT
            errorAsInt && !errorAsStr -> IsPreprocessorExprType.STR
            else -> IsPreprocessorExprType.ANY
        }
    }

    /** Whether the macro body is ill-typed when parameter [index] is bound to [probe] (others to ANY). */
    private fun bodyHasErrorWith(
        macro: IsPreprocessorExprFunctionMacroInfo,
        index: Int,
        probe: IsPreprocessorExprType,
    ): Boolean {
        val bound = macro.parameters.mapIndexed { i, p ->
            p to if (i == index) probe else IsPreprocessorExprType.ANY
        }.toMap()
        val ast = IsPreprocessorExprParser.parse(macro.body).ast
        val inference = IsPreprocessorExprTypeInference(
            referenceType = { bound[it] ?: typeOfReference(it, macro.order) },
            functionCallType = { name, argTypes -> functionCallType(name, argTypes, macro.order) },
            functionMacroParameterTypes = { anyParameterTypesOrNull(it) },
        )
        inference.infer(ast)
        return inference.errors.isNotEmpty()
    }

    /** Builds an inference whose references and calls are resolved against this resolver at [order]. */
    fun inferenceAt(order: Int): IsPreprocessorExprTypeInference =
        IsPreprocessorExprTypeInference(
            referenceType = { typeOfReference(it, order) },
            functionCallType = { name, argTypes -> functionCallType(name, argTypes, order) },
            functionMacroParameterTypes = { macroParameterTypesOrNull(it) },
        )

    /**
     * Result type of calling [name] with the given [argTypes] from a line at [beforeOrder]. A user function-
     * like macro declared earlier is inferred from its body with parameters bound to [argTypes]; otherwise the
     * built-in return type is used.
     */
    private fun functionCallType(
        name: String,
        argTypes: List<IsPreprocessorExprType>,
        beforeOrder: Int,
    ): IsPreprocessorExprType {
        val macro = functionMacros
            .filter { it.order < beforeOrder && it.name.equals(name, ignoreCase = true) }
            .maxByOrNull { it.order }
            ?: return builtinReturnType(name)

        val key = name.lowercase()
        if (key in visitingMacros) return IsPreprocessorExprType.ANY // residual cycle guard

        visitingMacros += key
        return try {
            val bound = macro.parameters.mapIndexedNotNull { i, p -> argTypes.getOrNull(i)?.let { p to it } }.toMap()
            inferMacroBodyType(macro, bound)
        } finally {
            visitingMacros -= key
        }
    }

    /** Type of [macro]'s body with its parameters bound to [bound]; inner errors are intentionally discarded. */
    private fun inferMacroBodyType(
        macro: IsPreprocessorExprFunctionMacroInfo,
        bound: Map<String, IsPreprocessorExprType>,
    ): IsPreprocessorExprType {
        val ast = IsPreprocessorExprParser.parse(macro.body).ast
        return IsPreprocessorExprTypeInference(
            referenceType = { bound[it] ?: typeOfReference(it, macro.order) },
            functionCallType = { name, argTypes -> functionCallType(name, argTypes, macro.order) },
            functionMacroParameterTypes = { anyParameterTypesOrNull(it) },
        ).infer(ast)
    }

    private fun inferDefineType(define: IsPreprocessorExprDefineInfo): IsPreprocessorExprType {
        val ast = IsPreprocessorExprParser.parse(define.expression).ast
        return inferenceAt(define.order).infer(ast)
    }
}
