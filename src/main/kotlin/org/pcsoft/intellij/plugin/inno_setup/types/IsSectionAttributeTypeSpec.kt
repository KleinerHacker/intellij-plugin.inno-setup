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

/**
 * Base type for the value model of a section attribute.
 *
 * The concrete subtype is selected from the `kind` property in the bundled section specification.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "kind")
@JsonSubTypes(
    JsonSubTypes.Type(value = IsSectionNativeTypeSpec::class, name = "native"),
    JsonSubTypes.Type(value = IsSectionReferenceTypeSpec::class, name = "reference"),
    JsonSubTypes.Type(value = IsSectionFlagTypeSpec::class, name = "flags"),
)
/**
 * Provides Inno Setup plugin behavior for the IntelliJ Platform.
 */
sealed class IsSectionAttributeTypeSpec

/**
 * Native scalar value type such as `string`, `boolean`, or `path`.
 *
 * @property dataType Name of the native type used by validators and completion providers.
 */
data class IsSectionNativeTypeSpec(val dataType: String) : IsSectionAttributeTypeSpec()

/**
 * Value type that references a named entry in another section.
 *
 * @property section Target section whose `Name` declarations can be referenced.
 */
data class IsSectionReferenceTypeSpec(val section: String) : IsSectionAttributeTypeSpec()

/**
 * Value type consisting of whitespace-separated flags.
 *
 * @property flags Allowed flags and their validation metadata.
 */
data class IsSectionFlagTypeSpec(val flags: List<IsSectionFlagSpec>) : IsSectionAttributeTypeSpec()
