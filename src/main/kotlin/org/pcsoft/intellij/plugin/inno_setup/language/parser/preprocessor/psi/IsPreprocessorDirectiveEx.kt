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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner

/**
 * Declares additional behavior mixed into generated PSI interfaces.
 */
interface IsPreprocessorDirectiveEx : PsiNameIdentifierOwner {
    /**
     * Returns or performs the public behavior represented by this member.
     */
    fun isDefine(): Boolean

    /** Whether this directive is an `#undef` (case-insensitive). */
    fun isUndef(): Boolean

    /** Whether this directive is an `#if` (case-insensitive). */
    fun isIf(): Boolean

    /** Whether this directive is an `#elif` (case-insensitive). */
    fun isElif(): Boolean

    /** Whether this directive is an `#else` (case-insensitive). */
    fun isElse(): Boolean

    /** Whether this directive is an `#endif` (case-insensitive). */
    fun isEndif(): Boolean

    /** Whether this directive is an `#ifdef` (case-insensitive). */
    fun isIfdef(): Boolean

    /** Whether this directive is an `#ifndef` (case-insensitive). */
    fun isIfndef(): Boolean

    /**
     * Whether this directive is `#ifdef` or `#ifndef` — the two openers whose argument is a single
     * identifier naming a (possibly non-existent) `#define`.
     */
    fun isIfdefFamily(): Boolean

    /** Whether this directive is an `#ifexist` (case-insensitive). */
    fun isIfExist(): Boolean

    /** Whether this directive is an `#ifnexist` (case-insensitive). */
    fun isIfNexist(): Boolean

    /**
     * Whether this directive is `#ifexist` or `#ifnexist` — the two openers whose argument is a string
     * expression naming a file.
     */
    fun isIfExistFamily(): Boolean

    /** Whether this directive is `#if` or `#elif` — the two that carry a boolean condition expression. */
    fun isIfElif(): Boolean

    /**
     * Whether this directive opens a conditional block that must be closed by `#endif`:
     * `#if`/`#ifdef`/`#ifndef`/`#ifexist`/`#ifnexist`.
     */
    fun isConditionalOpener(): Boolean

    /** Whether this directive is part of the conditional family (opener, `#elif`, `#else` or `#endif`). */
    fun isConditionalDirective(): Boolean

    /**
     * The condition expression of an `#if`/`#elif` (the whole value after the keyword, trimmed), or `null`
     * when this is not an `#if`/`#elif` or it has no condition.
     */
    fun getConditionExpressionText(): String?

    /**
     * Offset of [getConditionExpressionText] within this directive's text (for mapping analysis spans back
     * to editor ranges), or `-1` when there is no condition.
     */
    fun getConditionExpressionOffsetInDirective(): Int

    /**
     * The optional scope/visibility keyword (`public`/`protected`/`private`) preceding the name of a
     * `#define`/`#undef`, or `null` when none is present. Recognized only when an actual name follows it.
     */
    fun getVisibilityIdentifier(): PsiElement?
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

    // ── #dim / #redim arrays ──────────────────────────────────────────────────

    /** Whether this directive is a `#dim` (case-insensitive). */
    fun isDim(): Boolean

    /** Whether this directive is a `#redim` (case-insensitive). */
    fun isRedim(): Boolean

    /** Whether this directive is `#dim` or `#redim` — an array declaration. */
    fun isArrayDeclaration(): Boolean

    /** For a `#dim`/`#redim`: the array name (after the optional scope keyword), or `null`. */
    fun getArrayName(): String?

    /**
     * For a `#dim`/`#redim`: the size expression text between the `[…]`, or `null` when absent. For the offset
     * of this text within the directive use [getArraySizeOffsetInDirective].
     */
    fun getArraySizeText(): String?

    /** Offset of [getArraySizeText] within this directive's text, or `-1`. */
    fun getArraySizeOffsetInDirective(): Int

    /**
     * For a `#dim` with an inline initialiser `{v0, v1, …}`: the raw body between the outer braces (without the
     * braces), or `null` when there is none. For the offset use [getArrayInitializerOffsetInDirective].
     */
    fun getArrayInitializerText(): String?

    /** Offset of [getArrayInitializerText] within this directive's text, or `-1`. */
    fun getArrayInitializerOffsetInDirective(): Int

    /**
     * Whether this directive is a `#define Name[Index] Value` — an array element assignment (a `#define` whose
     * name is immediately followed by `[`).
     */
    fun isArrayElementDefine(): Boolean

    /** For an array element `#define Name[Index] …`: the array name `Name`, or `null`. */
    fun getDefineArrayName(): String?

    /**
     * For an array element `#define Name[Index] …`: the index expression text between the `[…]`, or `null`. For
     * the offset use [getDefineArrayIndexOffsetInDirective].
     */
    fun getDefineArrayIndexText(): String?

    /** Offset of [getDefineArrayIndexText] within this directive's text, or `-1`. */
    fun getDefineArrayIndexOffsetInDirective(): Int

    /** Whether this directive is a `#pragma` (case-insensitive). */
    fun isPragma(): Boolean

    /** For a `#pragma`: the sub-command keyword (first identifier of the value), or `null` when absent. */
    fun getPragmaSubCommand(): String?

    /**
     * For a `#pragma`: the raw argument text following the sub-command (no trimming, quotes kept), or
     * `null` when this is not a `#pragma` or there is no argument.
     */
    fun getPragmaArgumentText(): String?

    /**
     * Offset of [getPragmaArgumentText] within this directive's text (for mapping analysis spans back to
     * editor ranges), or `-1` when there is no argument.
     */
    fun getPragmaArgumentOffsetInDirective(): Int

    /** Whether this directive is an `#include` (case-insensitive). */
    fun isInclude(): Boolean

    /**
     * For an `#include` whose value is exactly a single quoted-string literal, that string PSI; `null` when
     * this is not an `#include` or its value is an expression / not a single literal string.
     */
    fun getIncludeLiteralString(): IsPreprocessorQuotedString?

    /** The inner text (path) of [getIncludeLiteralString], or `null`. */
    fun getIncludePath(): String?

    /**
     * For an `#ifexist`/`#ifnexist` whose value is exactly a single quoted-string literal, that string PSI;
     * `null` when this is not an `#ifexist`/`#ifnexist` or its value is an expression / not a single literal.
     */
    fun getExistLiteralString(): IsPreprocessorQuotedString?

    /** The inner text (path) of [getExistLiteralString], or `null`. */
    fun getExistPath(): String?

    /**
     * Resolves the literal `#ifexist`/`#ifnexist` filename to the file it points at, relative to the host
     * script's directory, or `null` when this is not an `#ifexist`/`#ifnexist`, the value is not a single
     * literal path, or the file does not exist. This is the foundation for a future "file (not) found"
     * diagnostic — no annotation consumes it yet.
     */
    fun resolveExistFile(): com.intellij.openapi.vfs.VirtualFile?
}
