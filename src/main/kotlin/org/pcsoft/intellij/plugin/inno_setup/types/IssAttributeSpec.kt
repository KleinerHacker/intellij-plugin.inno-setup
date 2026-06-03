package org.pcsoft.intellij.plugin.inno_setup.types

data class IssAttributeSpec(
    val name: String,
    val type: IssAttributeTypeSpec,
    val required: Boolean,
    val deprecated: Boolean,
    val description: String,
    val array: Boolean
)
