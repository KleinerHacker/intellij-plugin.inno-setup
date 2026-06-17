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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.parsing.psi

import com.intellij.psi.PsiNameIdentifierOwner

/**
 * Declares additional behavior mixed into generated PSI interfaces.
 */
interface IsPreprocessorDirectiveEx : PsiNameIdentifierOwner {
    /**
     * Returns or performs the public behavior represented by this member.
     */
    fun isDefine(): Boolean
    /**
     * Returns or performs the public behavior represented by this member.
     */
    fun getDefineName(): String?
    /**
     * Returns or performs the public behavior represented by this member.
     */
    fun getDefineValue(): String?

    /** A function-like macro: the name is immediately followed by `(` (no whitespace). */
    fun isFunctionMacro(): Boolean

    /** For function-like macros: the expression after the `(…)` parameter list, or `null` if absent. */
    fun getMacroBody(): String?

    /**
     * For function-like macros: the declared parameter names (e.g. `[a, b]` in `name(a,b)`), in order;
     * an empty list for a parameterless macro `name()` or when this directive is not a function-like macro.
     */
    fun getMacroParameters(): List<String>

    /**
     * The raw expression of this `#define` (no quote stripping): for a simple macro the value after the
     * name, for a function-like macro the body after the parameter list. `null` when there is none.
     */
    fun getDefineExpressionText(): String?

    /**
     * Offset of [getDefineExpressionText] within this directive's text (for mapping analysis spans back to
     * editor ranges), or `-1` when there is no expression.
     */
    fun getDefineExpressionOffsetInDirective(): Int

    /** Whether this directive is an `#include` (case-insensitive). */
    fun isInclude(): Boolean

    /**
     * For an `#include` whose value is exactly a single quoted-string literal, that string PSI; `null` when
     * this is not an `#include` or its value is an expression / not a single literal string.
     */
    fun getIncludeLiteralString(): IsPreprocessorQuotedString?

    /** The inner text (path) of [getIncludeLiteralString], or `null`. */
    fun getIncludePath(): String?
}
