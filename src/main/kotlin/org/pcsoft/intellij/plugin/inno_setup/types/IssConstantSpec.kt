package org.pcsoft.intellij.plugin.inno_setup.types

import com.fasterxml.jackson.annotation.JsonProperty

enum class IssConstantCategorySpec {
    @JsonProperty("directory")
    DIRECTORY,
    @JsonProperty("shell_folder")
    SHELL_FOLDER,
    @JsonProperty("auto")
    AUTO,
    @JsonProperty("special")
    SPECIAL,
    @JsonProperty("parameterized")
    PARAMETERIZED
}

data class IssBuiltinConstantSpec(
    val name: String,
    val description: String,
    val deprecated: Boolean,
    val category: IssConstantCategorySpec = IssConstantCategorySpec.SPECIAL,
    val parameterized: Boolean = false,
    val syntax: String? = null
)

data class IssConstantSpec(val constants: List<IssBuiltinConstantSpec>)
