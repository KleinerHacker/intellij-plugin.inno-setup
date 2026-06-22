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

package org.pcsoft.intellij.plugin.inno_setup.types

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Describes an Inno Setup preprocessor directive from the bundled ISPP specification.
 *
 * @property name Directive keyword without the leading `#`.
 * @property syntax Human-readable usage syntax shown in completion and documentation.
 * @property description HTML-capable description loaded from the specification file.
 * @property deprecated Targets for which the directive is marked as deprecated.
 * @property since First Inno Setup version that supports the directive, or `null` when unknown.
 * @property until Last Inno Setup version that supports the directive, or `null` when it is still valid.
 */
data class IsPreprocessorDirectiveSpec(
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val name: String,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val syntax: String,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val description: String,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val deprecated: Set<IsSectionSpecTarget> = emptySet(),
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val since: String? = null,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val until: String? = null
)

/**
 * Describes a predefined ISPP variable offered by completion and documentation.
 *
 * @property name Variable name as it appears in preprocessor expressions.
 * @property type Display type used in lookup tails and documentation.
 * @property description HTML-capable description loaded from the specification file.
 * @property since First Inno Setup version that provides the variable, or `null` when unknown.
 * @property until Last Inno Setup version that provides the variable, or `null` when it is still valid.
 */
enum class IsPreprocessorVariableType(val typeName: String) {
    /** Integer-valued predefined variable. */
    @JsonProperty("int")
    INT("int"),

    /** String-valued predefined variable. */
    @JsonProperty("str")
    STR("str"),

    /** Valueless macro (defined but carrying no value). */
    @JsonProperty("void")
    VOID("void"),
}

data class IsPreprocessorVariableSpec(
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val name: String,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val type: IsPreprocessorVariableType,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val description: String,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val since: String? = null,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val until: String? = null
)

/**
 * Result type of a built-in ISPP function, used by the expression type inference.
 */
enum class IsPreprocessorFunctionReturnType(val typeName: String) {
    /** Function returns an integer value. */
    @JsonProperty("int")
    INT("int"),

    /** Function returns a string value. */
    @JsonProperty("str")
    STR("str"),

    /** Function returns nothing (void / no usable value). */
    @JsonProperty("void")
    VOID("void"),

    /** Function return type is unknown or context dependent. */
    @JsonProperty("any")
    ANY("any"),
}

/**
 * Describes a built-in ISPP function offered by completion and documentation.
 *
 * @property name Function name without arguments.
 * @property signature Display signature including argument information.
 * @property returnType Structured result type used by the expression type inference.
 * @property description HTML-capable description loaded from the specification file.
 * @property since First Inno Setup version that provides the function, or `null` when unknown.
 * @property until Last Inno Setup version that provides the function, or `null` when it is still valid.
 */
data class IsPreprocessorFunctionSpec(
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val name: String,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val signature: String,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    @field:JsonProperty("return_type")
    val returnType: IsPreprocessorFunctionReturnType = IsPreprocessorFunctionReturnType.ANY,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val description: String,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val since: String? = null,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val until: String? = null
)

/**
 * Describes a reserved ISPP keyword that must not be used as a `#define` (or other macro) name.
 *
 * @property name Reserved keyword, matched case-insensitively against macro names.
 * @property description Human-readable reason why the name is reserved.
 * @property since First Inno Setup version in which the name became reserved, or `null` when unknown.
 * @property until Last Inno Setup version in which the name is reserved, or `null` when it still is.
 */
data class IsPreprocessorForbiddenNameSpec(
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val name: String,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val description: String,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val since: String? = null,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val until: String? = null
)

/**
 * Root model for the bundled ISPP specification YAML.
 *
 * @property directives All known preprocessor directives.
 * @property predefinedVariables All known predefined variables.
 * @property builtinFunctions All known built-in functions.
 * @property forbiddenVariableNames Reserved keywords that must not be used as macro names.
 */
data class IsPreprocessorSpec(
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val directives: List<IsPreprocessorDirectiveSpec>,
    @field:JsonProperty("predefined_variables") val predefinedVariables: List<IsPreprocessorVariableSpec>,
    @field:JsonProperty("builtin_functions") val builtinFunctions: List<IsPreprocessorFunctionSpec>,
    @field:JsonProperty("forbidden_variables_name")
    val forbiddenVariableNames: List<IsPreprocessorForbiddenNameSpec> = emptyList()
)
