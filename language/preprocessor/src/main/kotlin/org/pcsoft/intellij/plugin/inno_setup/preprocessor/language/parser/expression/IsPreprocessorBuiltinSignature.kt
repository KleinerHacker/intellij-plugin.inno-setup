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
 * Kind of a built-in function parameter as declared in the bundled ISPP specification.
 *
 * `INT`, `STR` and `ANY` are value parameters checked against the inferred argument type. `IDENT` and
 * `ARRAY` parameters are **not** evaluated by ISPP: the argument is the *name* of a symbol
 * (`Defined(X)`, `TypeOf(X)`, `DimOf(A)`), so it must be a bare identifier and must not be resolved as a
 * value.
 */
enum class IsPreprocessorBuiltinParameterKind {
    /** Integer-valued parameter (`Index: int`). */
    INT,

    /** String-valued parameter (`S: str`). */
    STR,

    /** Parameter accepting any value type (`Value: any`). */
    ANY,

    /** Un-evaluated symbol name (`Defined(Ident)`). */
    IDENT,

    /** Un-evaluated array name (`DimOf(Array)`). */
    ARRAY,
}

/**
 * A single parameter of a built-in ISPP function.
 *
 * @property name Parameter name as documented in the specification signature.
 * @property kind Declared kind of the parameter.
 * @property byRef Whether the parameter is passed by reference (`int*` / `str*`) and is therefore written
 *   back into the caller's macro — such an argument must be a bare macro name.
 * @property optional Whether the parameter has a default value and may be omitted.
 * @property defaultValue The default value as written in the signature (e.g. `0` or `""`), or `null` when
 *   the parameter is mandatory.
 */
data class IsPreprocessorBuiltinParameter(
    val name: String,
    val kind: IsPreprocessorBuiltinParameterKind,
    val byRef: Boolean = false,
    val optional: Boolean = false,
    val defaultValue: String? = null,
)

/**
 * The structured signature of a built-in ISPP function, derived from the `signature` string of the bundled
 * specification (e.g. `Exec(CmdLine: str, Params: str = "", …): int`).
 *
 * @property name Function name as called in the script.
 * @property parameters Declared parameters in order.
 * @property returnType Result type of the call.
 */
data class IsPreprocessorBuiltinSignature(
    val name: String,
    val parameters: List<IsPreprocessorBuiltinParameter>,
    val returnType: IsPreprocessorExprType,
) {

    /** Number of arguments that must be supplied (all parameters without a default value). */
    val requiredCount: Int get() = parameters.count { !it.optional }

    /** Maximum number of arguments accepted. */
    val maxCount: Int get() = parameters.size

    /** Human-readable description of the accepted argument count, e.g. `2 to 5 arguments`. */
    val arityDescription: String
        get() = when {
            maxCount == 0 -> "no arguments"
            requiredCount == maxCount -> "$requiredCount argument${if (requiredCount == 1) "" else "s"}"
            else -> "$requiredCount to $maxCount arguments"
        }
}

/**
 * Parses a specification signature string into an [IsPreprocessorBuiltinSignature], or returns `null` when
 * the string does not follow the `Name(params…)[: type]` shape.
 *
 * Supported parameter forms: `Name: type`, `Name: type*` (by reference), `Name: type = default` (optional),
 * plus the un-evaluated forms `Ident` and `Array`. [fallbackReturnType] is used when the signature carries
 * no result type (a `void` function).
 */
fun parseBuiltinSignature(
    signature: String,
    fallbackReturnType: IsPreprocessorExprType = IsPreprocessorExprType.ANY,
): IsPreprocessorBuiltinSignature? {
    val open = signature.indexOf('(')
    val close = signature.lastIndexOf(')')
    if (open < 0 || close < open) return null

    val name = signature.substring(0, open).trim()
    if (name.isEmpty()) return null

    val parameters = splitSignatureParameters(signature.substring(open + 1, close))
        .map { parseSignatureParameter(it) }

    val tail = signature.substring(close + 1).trim()
    val returnType = if (tail.startsWith(":")) {
        parseSignatureType(tail.removePrefix(":").trim())
    } else {
        fallbackReturnType
    }

    return IsPreprocessorBuiltinSignature(name, parameters, returnType)
}

/** Splits a parameter list at top-level commas, ignoring commas inside string defaults. */
private fun splitSignatureParameters(text: String): List<String> {
    if (text.isBlank()) return emptyList()
    val parts = mutableListOf<String>()
    val sb = StringBuilder()
    var inString = false
    text.forEach { c ->
        when {
            c == '"' -> {
                inString = !inString; sb.append(c)
            }

            c == ',' && !inString -> {
                parts += sb.toString(); sb.clear()
            }

            else -> sb.append(c)
        }
    }
    parts += sb.toString()
    return parts.map { it.trim() }.filter { it.isNotEmpty() }
}

/** Parses a single parameter declaration such as `Params: str = ""` or `Ident`. */
private fun parseSignatureParameter(text: String): IsPreprocessorBuiltinParameter {
    val optional = text.contains('=')
    val defaultValue = if (optional) text.substringAfter('=').trim().ifEmpty { null } else null
    val declaration = text.substringBefore('=').trim()
    val colon = declaration.indexOf(':')
    if (colon < 0) {
        // Un-evaluated symbol parameter: the declaration is the kind itself (`Ident` / `Array`).
        val kind = if (declaration.equals("Array", ignoreCase = true)) {
            IsPreprocessorBuiltinParameterKind.ARRAY
        } else {
            IsPreprocessorBuiltinParameterKind.IDENT
        }
        return IsPreprocessorBuiltinParameter(declaration, kind, false, optional, defaultValue)
    }

    val name = declaration.substring(0, colon).trim()
    val rawType = declaration.substring(colon + 1).trim()
    val byRef = rawType.endsWith("*")
    val kind = when (rawType.removeSuffix("*").trim().lowercase()) {
        "int" -> IsPreprocessorBuiltinParameterKind.INT
        "str" -> IsPreprocessorBuiltinParameterKind.STR
        else -> IsPreprocessorBuiltinParameterKind.ANY
    }
    return IsPreprocessorBuiltinParameter(name, kind, byRef, optional, defaultValue)
}

/** Maps a signature result type name to the expression type system. */
private fun parseSignatureType(text: String): IsPreprocessorExprType = when (text.lowercase()) {
    "int" -> IsPreprocessorExprType.INT
    "str" -> IsPreprocessorExprType.STR
    "void" -> IsPreprocessorExprType.VOID
    else -> IsPreprocessorExprType.ANY
}
