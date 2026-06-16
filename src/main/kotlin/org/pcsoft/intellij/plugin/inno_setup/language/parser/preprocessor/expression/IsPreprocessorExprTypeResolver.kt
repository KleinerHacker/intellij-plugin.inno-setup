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
 * Resolves the [IsPreprocessorExprType] of a referenced identifier by recursively inferring the type of the
 * `#define` it points at — so a type error like `str * int` is detected even when the operands are
 * themselves other `#define`s (e.g. `#define A "x"` / `#define B 5` / `#define C A * B`).
 *
 * Two independent safeguards prevent infinite recursion:
 *  1. **Declaration order** — a reference only resolves to a `#define` declared *before* the referencing
 *     line ([beforeOrder]). A well-formed script can therefore never form a reference ring, because every
 *     macro must already exist earlier to be referenced.
 *  2. **Cycle guard** — a `visiting` set plus memoization break any residual cycle (e.g. a self-reference or
 *     an out-of-order script) by yielding [IsPreprocessorExprType.ANY] instead of recursing forever.
 *
 * @param defines all simple (non function-like) `#define`s of the file.
 * @param variableType resolves a predefined variable name to its type, or `null` if it is not one.
 * @param functionReturnType resolves a built-in function name to its return type.
 */
class IsPreprocessorExprTypeResolver(
    defines: List<IsPreprocessorExprDefineInfo>,
    private val variableType: (String) -> IsPreprocessorExprType? = { null },
    private val functionReturnType: (String) -> IsPreprocessorExprType = { IsPreprocessorExprType.ANY },
) {

    private val defines: List<IsPreprocessorExprDefineInfo> = defines.sortedBy { it.order }
    private val cache = HashMap<String, IsPreprocessorExprType>()
    private val visiting = HashSet<String>()

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

    /** Builds an inference whose references are resolved against this resolver at [order]. */
    fun inferenceAt(order: Int): IsPreprocessorExprTypeInference =
        IsPreprocessorExprTypeInference(
            referenceType = { typeOfReference(it, order) },
            functionReturnType = functionReturnType,
        )

    private fun inferDefineType(define: IsPreprocessorExprDefineInfo): IsPreprocessorExprType {
        val ast = IsPreprocessorExprParser.parse(define.expression).ast
        return inferenceAt(define.order).infer(ast)
    }
}
