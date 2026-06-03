package org.pcsoft.intellij.plugin.inno_setup.types

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "kind")
@JsonSubTypes(
    JsonSubTypes.Type(value = IssNativeTypeSpec::class, name = "native"),
    JsonSubTypes.Type(value = IssReferenceTypeSpec::class, name = "reference"),
    JsonSubTypes.Type(value = IssFlagTypeSpec::class, name = "flags"),
)
sealed class IssAttributeTypeSpec

data class IssNativeTypeSpec(val dataType: String) : IssAttributeTypeSpec()
data class IssReferenceTypeSpec(val section: String) : IssAttributeTypeSpec()
data class IssFlagTypeSpec(val flags: List<IssFlagSpec>) : IssAttributeTypeSpec()
