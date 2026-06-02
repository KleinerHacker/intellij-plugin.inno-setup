package org.pcsoft.intellij.plugin.inno_setup.types

data class IssFlagConflict(
    val flag: String,
    val severity: IssFlagSeverity
)

data class IssFlag(
    val name: String,
    val description: String,
    val deprecated: Boolean,
    val conflicts: List<IssFlagConflict> = emptyList()
)
