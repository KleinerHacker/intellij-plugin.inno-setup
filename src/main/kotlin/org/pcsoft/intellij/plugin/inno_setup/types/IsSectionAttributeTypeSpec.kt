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
    JsonSubTypes.Type(value = IsSectionNativeTypeSpec::class, name = "native"),
    JsonSubTypes.Type(value = IsSectionReferenceTypeSpec::class, name = "reference"),
    JsonSubTypes.Type(value = IsSectionFlagTypeSpec::class, name = "flags"),
)
sealed class IsSectionAttributeTypeSpec

data class IsSectionNativeTypeSpec(val dataType: String) : IsSectionAttributeTypeSpec()
data class IsSectionReferenceTypeSpec(val section: String) : IsSectionAttributeTypeSpec()
data class IsSectionFlagTypeSpec(val flags: List<IsSectionFlagSpec>) : IsSectionAttributeTypeSpec()
