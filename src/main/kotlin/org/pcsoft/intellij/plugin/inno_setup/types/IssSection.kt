package org.pcsoft.intellij.plugin.inno_setup.types

data class IssSection(
    val name: String,
    val type: String,
    val deprecated: Boolean,
    val description: String,
    val attributes: List<IssAttribute>
)
