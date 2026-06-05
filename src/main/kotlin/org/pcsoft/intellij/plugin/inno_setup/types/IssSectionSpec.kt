package org.pcsoft.intellij.plugin.inno_setup.types

data class IssSectionSpec(
    val name: String,
    val type: String,
    val required: Boolean = false,
    val deprecated: Boolean,
    val description: String,
    val attributes: List<IssAttributeSpec>
)
