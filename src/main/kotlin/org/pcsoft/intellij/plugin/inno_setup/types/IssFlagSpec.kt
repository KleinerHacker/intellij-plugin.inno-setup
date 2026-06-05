package org.pcsoft.intellij.plugin.inno_setup.types

data class IssFlagConflictSpec(
    val flag: String,
    val severity: IssFlagSeveritySpec
)

data class IssFlagSpec(
    val name: String,
    val description: String,
    val deprecated: Boolean,
    val conflicts: List<IssFlagConflictSpec> = emptyList()
)
