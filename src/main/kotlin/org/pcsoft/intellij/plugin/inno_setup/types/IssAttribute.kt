package org.pcsoft.intellij.plugin.inno_setup.types

data class IssAttribute(
    val name: String,
    val type: IssAttributeType,
    val required: Boolean,
    val deprecated: Boolean,
    val description: String,
    val array: Boolean
)
