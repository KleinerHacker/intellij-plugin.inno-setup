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
 * A single declared parameter of a function-like `#define` macro.
 *
 * ISPP declares a parameter as `[<type>] <name> [= <default>]` for a by-value parameter and
 * `[<type>] * <name>` for a by-reference one, with `<type>` being `any`, `int`, `str` or `func`.
 *
 * @property name Parameter name as written in the declaration.
 * @property declaredType Type from the declaration, or `null` when none was written — only then is the type
 *   probed from the macro body.
 * @property byRef Whether the parameter is declared `*` and therefore written back into the caller's macro;
 *   such an argument must be a bare macro name.
 * @property defaultValue The default expression as written (`= <expr>`), or `null` for a mandatory parameter.
 * @property offset Offset of [name] inside the raw parameter list, used to anchor references and rename.
 */
data class IsPreprocessorMacroParameter(
    val name: String,
    val declaredType: IsPreprocessorExprType? = null,
    val byRef: Boolean = false,
    val defaultValue: String? = null,
    val offset: Int = -1,
) {

    /** Whether the parameter may be omitted at a call site (it carries a default value). */
    val optional: Boolean get() = defaultValue != null

    /** The declaration rendered like ISPP writes it, e.g. `int *Result` or `str S = ""`. */
    val presentation: String
        get() = buildString {
            declaredType?.let { append(it.name.lowercase()).append(' ') }
            if (byRef) append('*')
            append(name)
            defaultValue?.let { append(" = ").append(it) }
        }
}

/**
 * Parses the raw parameter list of a function-like `#define` (the text between `(` and `)`) into the declared
 * parameters, in order. An empty or blank list yields an empty result.
 *
 * The list is split at top-level commas only, so a default value containing a call (`= Copy(S, 1, 2)`), an
 * index or a string with commas stays in one piece.
 */
fun parseMacroParameters(rawParameterList: String): List<IsPreprocessorMacroParameter> =
    splitMacroParameters(rawParameterList).mapNotNull { (text, offset) -> parseMacroParameter(text, offset) }

/**
 * The structured signature of the function-like macro [name] with the parameters [parameters] and the result
 * type [returnType], reusing the built-in signature model so macro calls are validated exactly like built-in
 * calls (argument count including defaults, argument types, by-reference arguments).
 *
 * A parameter without a declared type becomes [IsPreprocessorBuiltinParameterKind.ANY]; [probedTypes] may
 * supply the type inferred from the macro body for those positions.
 */
fun macroSignatureOf(
    name: String,
    parameters: List<IsPreprocessorMacroParameter>,
    returnType: IsPreprocessorExprType = IsPreprocessorExprType.ANY,
    probedTypes: List<IsPreprocessorExprType> = emptyList(),
): IsPreprocessorBuiltinSignature =
    IsPreprocessorBuiltinSignature(
        name,
        parameters.mapIndexed { index, parameter ->
            val type = parameter.declaredType
                ?: probedTypes.getOrNull(index)
                ?: IsPreprocessorExprType.ANY
            IsPreprocessorBuiltinParameter(
                parameter.name,
                type.toParameterKind(),
                parameter.byRef,
                parameter.optional,
                parameter.defaultValue,
            )
        },
        returnType,
    )

/** The parameter kind a declared macro parameter type maps to. */
private fun IsPreprocessorExprType.toParameterKind(): IsPreprocessorBuiltinParameterKind = when (this) {
    IsPreprocessorExprType.INT -> IsPreprocessorBuiltinParameterKind.INT
    IsPreprocessorExprType.STR -> IsPreprocessorBuiltinParameterKind.STR
    else -> IsPreprocessorBuiltinParameterKind.ANY
}

/**
 * Splits a raw macro parameter list at top-level commas, returning each part with its offset in
 * [rawParameterList]. Commas inside strings, parentheses or brackets are not separators.
 */
private fun splitMacroParameters(rawParameterList: String): List<Pair<String, Int>> {
    if (rawParameterList.isBlank()) return emptyList()
    val parts = mutableListOf<Pair<String, Int>>()
    val current = StringBuilder()
    var start = 0
    var depth = 0
    var quote: Char? = null
    rawParameterList.forEachIndexed { index, c ->
        when {
            quote != null -> {
                if (c == quote) quote = null
                current.append(c)
            }

            c == '"' || c == '\'' -> {
                quote = c; current.append(c)
            }

            c == '(' || c == '[' -> {
                depth++; current.append(c)
            }

            c == ')' || c == ']' -> {
                depth--; current.append(c)
            }

            c == ',' && depth == 0 -> {
                parts += current.toString() to start
                current.clear()
                start = index + 1
            }

            else -> current.append(c)
        }
    }
    parts += current.toString() to start
    return parts
}

/** The ISPP type keywords a parameter declaration may start with. */
private val MACRO_PARAMETER_TYPES = mapOf(
    "any" to IsPreprocessorExprType.ANY,
    "int" to IsPreprocessorExprType.INT,
    "str" to IsPreprocessorExprType.STR,
    // `func` declares a macro-valued parameter; the expression type system has no function type, so any
    // value is accepted rather than producing a wrong type error.
    "func" to IsPreprocessorExprType.ANY,
)

/**
 * Parses one parameter declaration; [partOffset] is its offset inside the raw parameter list.
 *
 * The declaration is scanned left to right — optional type keyword, optional `*`, name — so the reported name
 * offset is the real one even when the name repeats a character of the type keyword (`str s`).
 */
private fun parseMacroParameter(text: String, partOffset: Int): IsPreprocessorMacroParameter? {
    val equals = topLevelEquals(text)
    val defaultValue = if (equals >= 0) text.substring(equals + 1).trim().ifEmpty { null } else null
    val end = if (equals >= 0) equals else text.length

    var index = 0
    fun skipWhitespace() {
        while (index < end && text[index].isWhitespace()) index++
    }

    skipWhitespace()
    var declaredType: IsPreprocessorExprType? = null
    val typeMatch = MACRO_PARAMETER_TYPE_PREFIX.find(text.substring(index, end))
    if (typeMatch != null) {
        declaredType = MACRO_PARAMETER_TYPES[typeMatch.value.lowercase()]
        index += typeMatch.value.length
    }

    skipWhitespace()
    val byRef = index < end && text[index] == '*'
    if (byRef) index++

    skipWhitespace()
    val nameStart = index
    while (index < end && (text[index].isLetterOrDigit() || text[index] == '_')) index++
    val name = text.substring(nameStart, index)
    if (name.isEmpty()) return null

    return IsPreprocessorMacroParameter(name, declaredType, byRef, defaultValue, partOffset + nameStart)
}

/**
 * `any`/`int`/`str`/`func` written as a separate leading word of a parameter declaration — only a match that
 * is followed by more text is a type, so a parameter simply named `Int` stays a name.
 */
private val MACRO_PARAMETER_TYPE_PREFIX = Regex("^(any|int|str|func)(?=[\\s*])", RegexOption.IGNORE_CASE)

/** Index of the `=` that introduces the default value, ignoring `==`/`!=`/`<=`/`>=` inside a default. */
private fun topLevelEquals(text: String): Int {
    var depth = 0
    var quote: Char? = null
    text.forEachIndexed { index, c ->
        when {
            quote != null -> if (c == quote) quote = null
            c == '"' || c == '\'' -> quote = c
            c == '(' || c == '[' -> depth++
            c == ')' || c == ']' -> depth--
            c == '=' && depth == 0 -> {
                val previous = text.getOrNull(index - 1)
                val next = text.getOrNull(index + 1)
                if (previous !in COMPARISON_CHARS && next != '=') return index
            }
        }
    }
    return -1
}

private val COMPARISON_CHARS = setOf('=', '!', '<', '>')
