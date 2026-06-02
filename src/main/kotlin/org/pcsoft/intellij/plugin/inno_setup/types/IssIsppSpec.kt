package org.pcsoft.intellij.plugin.inno_setup.types

import com.fasterxml.jackson.annotation.JsonProperty

data class IssIsppDirective(
    val name: String,
    val syntax: String,
    val description: String,
    val deprecated: Boolean = false
)

data class IssIsppVariable(
    val name: String,
    val type: String,
    val description: String
)

data class IssIsppFunction(
    val name: String,
    val signature: String,
    val description: String
)

data class IssIsppSpec(
    val directives: List<IssIsppDirective>,
    @field:JsonProperty("predefined_variables") val predefinedVariables: List<IssIsppVariable>,
    @field:JsonProperty("builtin_functions") val builtinFunctions: List<IssIsppFunction>
)
