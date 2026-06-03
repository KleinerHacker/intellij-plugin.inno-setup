package org.pcsoft.intellij.plugin.inno_setup.types

import com.fasterxml.jackson.annotation.JsonProperty

data class IssIsppDirectiveSpec(
    val name: String,
    val syntax: String,
    val description: String,
    val deprecated: Boolean = false
)

data class IssIsppVariableSpec(
    val name: String,
    val type: String,
    val description: String
)

data class IssIsppFunctionSpec(
    val name: String,
    val signature: String,
    val description: String
)

data class IssIsppSpec(
    val directives: List<IssIsppDirectiveSpec>,
    @field:JsonProperty("predefined_variables") val predefinedVariables: List<IssIsppVariableSpec>,
    @field:JsonProperty("builtin_functions") val builtinFunctions: List<IssIsppFunctionSpec>
)
