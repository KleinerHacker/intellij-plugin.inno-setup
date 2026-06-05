/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

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
