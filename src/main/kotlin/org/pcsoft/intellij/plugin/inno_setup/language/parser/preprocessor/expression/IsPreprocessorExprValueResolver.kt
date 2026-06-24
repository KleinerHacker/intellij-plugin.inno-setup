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

/**
 * Computes the static value of a `#define` expression, resolving references to other simple `#define`s and
 * calls to function-like macros recursively — so `#define A 10` / `#define B A + 5` lets `B` evaluate to `15`,
 * and `#define f(x) "a" + x` / `#define C f("b")` lets `C` evaluate to `"ab"`.
 *
 * Mirrors [IsPreprocessorExprTypeResolver]: a reference/call only resolves to a `#define`/macro declared
 * *before* the referencing line (declaration order), and the `visiting`/`visitingMacros` guards break
 * residual cycles by yielding `null` (value not computable).
 *
 * @param defines all simple (non function-like) `#define`s of the file.
 * @param functionMacros all function-like macros of the file.
 */
class IsPreprocessorExprValueResolver(
    defines: List<IsPreprocessorExprDefineInfo>,
    functionMacros: List<IsPreprocessorExprFunctionMacroInfo> = emptyList(),
    arrayElements: List<IsPreprocessorExprArrayElementInfo> = emptyList(),
) {

    private val defines: List<IsPreprocessorExprDefineInfo> = defines.sortedBy { it.order }
    private val functionMacros: List<IsPreprocessorExprFunctionMacroInfo> = functionMacros.sortedBy { it.order }
    private val arrayElements: List<IsPreprocessorExprArrayElementInfo> = arrayElements.sortedBy { it.order }
    private val visiting = HashSet<String>()
    private val visitingMacros = HashSet<String>()
    private val visitingElements = HashSet<String>()

    /** Value of [expression] evaluated as if it appeared on a line at position [order]. */
    fun evaluate(expression: String, order: Int): IsPreprocessorExprValue? =
        evaluatorFor(expression, order).evaluate(IsPreprocessorExprParser.parse(expression).ast)

    /** Value of the identifier [name] referenced from a line at [beforeOrder], or `null` when not computable. */
    fun valueOfReference(name: String, beforeOrder: Int): IsPreprocessorExprValue? {
        val key = name.lowercase()
        if (key in visiting) return null // residual cycle guard

        val target = defines
            .filter { it.order < beforeOrder && it.name.equals(name, ignoreCase = true) }
            .maxByOrNull { it.order }
            ?: return null

        visiting += key
        return try {
            evaluate(target.expression, target.order)
        } finally {
            visiting -= key
        }
    }

    /**
     * Value of the array element `name[index]` read from a line at [beforeOrder]: the nearest preceding
     * `#define name[index]` (or inline-initialiser element) of that exact index is evaluated. `null` when there
     * is no such assignment, the value is not computable, or a cycle is detected.
     */
    fun arrayElementValue(name: String, index: Long, beforeOrder: Int): IsPreprocessorExprValue? {
        val target = arrayElements
            .filter { it.order < beforeOrder && it.name.equals(name, ignoreCase = true) && it.index == index }
            .maxByOrNull { it.order }
            ?: return null

        val key = "${name.lowercase()}[$index]"
        if (key in visitingElements) return null // residual cycle guard

        visitingElements += key
        return try {
            evaluate(target.expression, target.order)
        } finally {
            visitingElements -= key
        }
    }

    /**
     * Value of calling the function-like macro [name] with the evaluated [argValues], from a line at
     * [beforeOrder]. The macro body is evaluated with its parameters bound to the argument values; `null`
     * when the macro is unknown, an argument is not computable, or a cycle is detected.
     */
    private fun callValue(
        name: String,
        argValues: List<IsPreprocessorExprValue?>,
        beforeOrder: Int,
    ): IsPreprocessorExprValue? {
        val macro = functionMacros
            .filter { it.order < beforeOrder && it.name.equals(name, ignoreCase = true) }
            .maxByOrNull { it.order }
            ?: return null

        val bound = macro.parameters.mapIndexedNotNull { i, p -> argValues.getOrNull(i)?.let { p to it } }.toMap()
        if (bound.size != macro.parameters.size) return null // an argument was not computable / wrong arity

        val key = name.lowercase()
        if (key in visitingMacros) return null // residual cycle guard

        visitingMacros += key
        return try {
            evaluatorFor(macro.body, macro.order, boundParams = bound)
                .evaluate(IsPreprocessorExprParser.parse(macro.body).ast)
        } finally {
            visitingMacros -= key
        }
    }

    /**
     * An evaluator over [text] for a line at [order]; references resolve to [boundParams] first (macro
     * parameters) then to other `#define`s declared before [order], and calls are dispatched to [callValue].
     */
    private fun evaluatorFor(
        text: String,
        order: Int,
        boundParams: Map<String, IsPreprocessorExprValue> = emptyMap(),
    ): IsPreprocessorExprEvaluator =
        IsPreprocessorExprEvaluator(
            text = text,
            referenceValue = { name -> boundParams[name] ?: valueOfReference(name, order) },
            callValue = { name, args -> callValue(name, args, order) },
            arrayElementValue = { name, index -> arrayElementValue(name, index, order) },
        )
}
