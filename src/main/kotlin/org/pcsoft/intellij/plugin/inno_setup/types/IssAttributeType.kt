package org.pcsoft.intellij.plugin.inno_setup.types

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "kind")
@JsonSubTypes(
    JsonSubTypes.Type(value = IssNativeType::class, name = "native"),
    JsonSubTypes.Type(value = IssReferenceType::class, name = "reference")
)
sealed class IssAttributeType

data class IssNativeType(val dataType: String) : IssAttributeType()
data class IssReferenceType(val section: String) : IssAttributeType()
